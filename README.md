# CINEGLOW — App de Catálogo de Filmes e Séries

Aplicativo Android nativo (Java) para pesquisar filmes e séries, ver detalhes e salvar
títulos em três listas pessoais — **Favoritos**, **Assistindo** e **Watchlist** (Assistir
Mais Tarde). A autenticação e o armazenamento das listas ficam no **Supabase**, e os dados
de filmes/séries vêm da **API da Trakt**.

> Projeto acadêmico (prova de Mobile). O código foi escrito para ser **fácil de ler**,
> com responsabilidades separadas em *controllers*, *services* e *models*.

---

## Sumário

1. [Visão geral e tecnologias](#1-visão-geral-e-tecnologias)
2. [Arquitetura em camadas](#2-arquitetura-em-camadas)
3. [Estrutura de pastas](#3-estrutura-de-pastas)
4. [Como configurar e rodar](#4-como-configurar-e-rodar)
5. [Banco de dados (Supabase)](#5-banco-de-dados-supabase)
6. [Explicação detalhada do código](#6-explicação-detalhada-do-código)
7. [Fluxos completos](#7-fluxos-completos-passo-a-passo)
8. [Segurança](#8-segurança)
9. [Usuários de teste](#9-usuários-de-teste)
10. [O que ainda é evolução futura](#10-o-que-ainda-é-evolução-futura)

---

## 1. Visão geral e tecnologias

| Item | Descrição |
|------|-----------|
| Plataforma | Android nativo (minSdk 24 / Android 7.0+) |
| Linguagem | Java |
| IDE | Android Studio |
| Autenticação + Banco | Supabase (Auth + PostgreSQL + RLS) |
| API de filmes/séries | Trakt — `https://api.trakt.tv` |
| Rede (HTTP) | Retrofit 2 + OkHttp + Gson |
| Imagens (pôsteres) | Glide |
| Listas na tela | RecyclerView |
| Token seguro | EncryptedSharedPreferences |

A ideia central: **a tela nunca conversa direto com a internet**. Ela chama um *Controller*,
que chama a camada de *rede* (Retrofit), que fala com o Supabase ou a Trakt. Isso deixa o
código organizado, testável e fácil de manter.

---

## 2. Arquitetura em camadas

```
┌─────────────────────────────────────────────────────────────┐
│  ACTIVITIES (telas)                                          │
│  Login, Signup, Splash, Menu, Main(Busca), Detalhes,        │
│  Favoritos, Assistindo, Watchlist                            │
│  → só cuidam da interface e reagem a cliques                 │
└───────────────┬─────────────────────────────────────────────┘
                │ chamam
┌───────────────▼─────────────────────────────────────────────┐
│  CONTROLLERS (regras / orquestração)                        │
│  AuthController, TraktController, FavoritosController,      │
│  AssistindoController, WatchlistController, SessionController│
│  → montam as requisições e devolvem o resultado por callback │
└───────────────┬─────────────────────────────────────────────┘
                │ usam
┌───────────────▼─────────────────────────────────────────────┐
│  NETWORK (Retrofit)                                         │
│  ApiClient (fábrica), SupabaseService, SupabaseDataService, │
│  TraktService, TokenAuthenticator                           │
└───────────────┬─────────────────────────────────────────────┘
                │ HTTP
┌───────────────▼─────────────────────────────────────────────┐
│  SERVIÇOS EXTERNOS:  Supabase   e   Trakt                    │
└─────────────────────────────────────────────────────────────┘

         MODELS (DTOs) circulam entre todas as camadas:
         AuthModels, Titulo, TraktBusca, ItemSalvo
```

**Padrão de comunicação assíncrona:** como chamadas de rede não podem travar a tela, todo
*Controller* recebe um **callback** (uma interface com `onSucesso(...)` e `onErro(...)`).
O Retrofit faz a chamada em segundo plano com `enqueue(...)` e devolve o resultado na thread
principal, onde a tela atualiza com segurança.

---

## 3. Estrutura de pastas

```
app/src/main/
├── java/com/example/bgl/
│   ├── SplashActivity.java        # decide: logado → Menu, senão → Login
│   ├── LoginActivity.java         # tela de login
│   ├── SignupActivity.java        # tela de cadastro
│   ├── MenuActivity.java          # hub: leva às 4 telas
│   ├── MainActivity.java          # tela de BUSCA (Trakt + RecyclerView)
│   ├── DetalhesActivity.java      # detalhes do título + botões de salvar
│   ├── FavoritosActivity.java     # lista de favoritos
│   ├── AssistindoActivity.java    # lista "assistindo"
│   ├── WatchlistActivity.java     # lista "assistir mais tarde"
│   ├── Ui.java                    # utilitário de edge-to-edge (insets)
│   │
│   ├── adapter/
│   │   ├── TituloAdapter.java     # itens dos resultados de busca
│   │   └── ItemSalvoAdapter.java  # itens das listas salvas
│   │
│   ├── controller/
│   │   ├── SessionController.java     # guarda o token (criptografado)
│   │   ├── AuthController.java        # login / cadastro / logout
│   │   ├── TraktController.java       # busca na Trakt
│   │   ├── FavoritosController.java   # listar/adicionar favoritos
│   │   ├── AssistindoController.java  # listar/adicionar assistindo
│   │   ├── WatchlistController.java   # listar/adicionar watchlist
│   │   └── ListaCallback.java         # callback compartilhado das listas
│   │
│   ├── model/
│   │   ├── AuthModels.java        # DTOs de autenticação
│   │   ├── Titulo.java            # filme/série (resultado de busca)
│   │   ├── TraktBusca.java        # formato cru da resposta da Trakt (busca)
│   │   ├── TraktPessoas.java      # resposta do elenco da Trakt
│   │   └── ItemSalvo.java         # título salvo em uma lista
│   │
│   └── network/
│       ├── ApiClient.java             # fábrica única dos clientes Retrofit
│       ├── SupabaseService.java       # endpoints de Auth do Supabase
│       ├── SupabaseDataService.java   # endpoints das tabelas (REST)
│       ├── TraktService.java          # endpoint de busca da Trakt
│       └── TokenAuthenticator.java    # renova o token no 401
│
├── res/layout/    # XMLs de todas as telas + itens de lista
├── res/drawable/  # fundo cinematográfico, vidro, ícones, logo
├── res/anim/      # transições de tela e animação em cascata das listas
├── res/values/    # cores, strings e tema (sempre escuro, liquid glass)
└── AndroidManifest.xml
```

---

## 4. Como configurar e rodar

### 4.1 Chaves no `app/build.gradle.kts`

As chaves ficam em `buildConfigField`, acessíveis no código via `BuildConfig`:

```kotlin
buildConfigField("String", "SUPABASE_URL", "\"https://SEU-PROJETO.supabase.co\"")
buildConfigField("String", "SUPABASE_KEY", "\"sb_publishable_...\"")   // chave publishable
buildConfigField("String", "TRAKT_CLIENT_ID", "\"SEU_CLIENT_ID_TRAKT\"")
```

- **SUPABASE_KEY** é a *publishable* (pública por design). Quem protege os dados é o **RLS**.
- **TRAKT_CLIENT_ID** vem de https://trakt.tv/oauth/applications.


### 4.2 Rodar

Abra no **Android Studio**, faça **Sync Project with Gradle Files** e clique em **Run**.

---

## 5. Banco de dados (Supabase)

### Tabelas

- **`profiles`** — perfil do usuário (id, email, nome). O `id` referencia `auth.users`.
- **`favoritos`**, **`assistindo`**, **`watchlist`** — as três listas. Colunas principais:
  `user_id`, `tmdb_id` (guarda o id da Trakt), `tipo` (`'movie'` ou `'tv'`), `titulo`,
  `poster_url`, datas e `updated_at`. A `assistindo` ainda tem `temporada_atual`/`episodio_atual`.

### Mecanismos automáticos (triggers)

1. **`set_updated_at`** — atualiza a coluna `updated_at` sozinha a cada `UPDATE`.
2. **`mover_entre_listas`** — ao inserir em `assistindo`, remove o mesmo título de
   `watchlist` (e vice-versa). Garante a regra "não pode estar nas duas ao mesmo tempo".

### Segurança no banco (RLS)

Todas as tabelas têm **Row Level Security** ligado, com políticas `auth.uid() = user_id`.
Tradução: **cada usuário só enxerga e altera as próprias linhas**, mesmo usando a mesma
chave pública. A trava `UNIQUE(user_id, tmdb_id, tipo)` impede duplicatas e é o que permite
o *upsert* do app (ver `SupabaseDataService`).

---

## 6. Explicação detalhada do código

### 6.1 `model` (modelos de dados)

Modelos são "caixinhas" de dados (DTOs). O **Gson** preenche os campos automaticamente a
partir do JSON da resposta. Quando o nome do campo no JSON é diferente do nome em Java,
usamos `@SerializedName`.

#### `AuthModels.java`
Agrupa os DTOs de autenticação:
- **`SignUpRequest`** — corpo do cadastro: `email`, `password` e um objeto `data` com o
  `nome` (vai como *user metadata* e é usado para preencher o perfil).
- **`LoginRequest`** — `email` + `password`.
- **`RefreshRequest`** — `refresh_token` (usado para renovar a sessão).
- **`AuthResponse`** — resposta do Supabase: `access_token`, `refresh_token`, `expires_in`
  (segundos até expirar) e o objeto `user`.
- **`User`** — `id` (uuid) e `email`.

#### `Titulo.java`
Representa um **filme ou série** vindo da busca. Como a Trakt usa nomes diferentes
("title" para filme, "name" para série), a classe esconde isso com métodos auxiliares:
- **`getTituloExibicao()`** — devolve o título certo (filme ou série).
- **`getAno()`** — extrai os 4 primeiros dígitos da data de lançamento.
- **`getTipo()`** — devolve `"movie"` ou `"tv"` (converte o `"show"` da Trakt para `"tv"`,
  que é o valor aceito pelo banco).
- **`getPosterUrl()`** — monta a URL do pôster (a Trakt manda o caminho sem `https://`,
  então adicionamos quando falta).
- Implementa `Serializable` para o objeto inteiro poder ser enviado de uma tela para outra
  via `Intent` (forma simples, sem `Parcelable`).

#### `TraktBusca.java`
É o formato **cru** da resposta de busca da Trakt. Cada item tem um `type` (`"movie"` ou
`"show"`) e o objeto correspondente preenchido (`movie` ou `show`). Classes internas:
`TraktItem` (title, year, overview, rating, ids, images), `Ids` e `Images`.
O método **`getItem()`** devolve o objeto certo (filme ou série). Este modelo é convertido
para `Titulo` dentro do `TraktController`.

#### `ItemSalvo.java`
Representa um título **salvo** em uma das listas no Supabase. Campos com `@SerializedName`
batendo com as colunas: `user_id`, `tmdb_id`, `tipo`, `titulo`, `poster_url`. Tem o helper
`getTipoAmigavel()` que devolve "Filme" ou "Série" para mostrar na tela.

#### `TraktPessoas.java`
Resposta do endpoint de elenco da Trakt. Usamos a lista `cast` (atores); de cada entrada
pegamos `person.name` (o nome do ator) para exibir na tela de Detalhes.

---

### 6.2 `network` (camada de rede)

#### `ApiClient.java` — a fábrica
Centraliza a criação dos clientes Retrofit (padrão *singleton* — cria uma vez e reaproveita).
- **`getSupabase()`** — cliente de **Auth** (login/cadastro/logout/refresh). Um *interceptor*
  injeta os headers `apikey` e `Content-Type` em toda chamada. **Não tem** renovação de token
  (para evitar loop quando o próprio refresh é chamado).
- **`getSupabaseData()`** — cliente das **tabelas**. Igual ao de cima, mas com um
  **`TokenAuthenticator`** que renova o token quando dá 401.
- **`getTrakt()`** — cliente da Trakt. O interceptor injeta os headers obrigatórios:
  `trakt-api-key` (o Client ID) e `trakt-api-version: 2`.
- **`init(Context)`** — guarda uma `SessionController` que o `TokenAuthenticator` usa para
  ler/gravar o token. Os controllers de lista chamam isso antes de usar o cliente de dados.
- **`supabaseClientBuilder()`** / **`logging()`** — métodos auxiliares que evitam repetição
  (headers do Supabase e log das requisições só no modo DEBUG).

#### `SupabaseService.java` — endpoints de Auth
Interface Retrofit com os endpoints do GoTrue (Auth do Supabase):
- `cadastrar(...)` → `POST /auth/v1/signup`
- `login(grant, ...)` → `POST /auth/v1/token?grant_type=password`
- `renovar(grant, ...)` → `POST /auth/v1/token?grant_type=refresh_token`
- `logout(bearer)` → `POST /auth/v1/logout`

#### `SupabaseDataService.java` — endpoints das tabelas (PostgREST)
Lê e grava nas tabelas via API REST do Supabase. Para cada lista há um `GET` (listar), um
`POST` (inserir) e um `DELETE` (remover, filtrando por `tmdb_id` e `tipo`). Dois detalhes
importantes nos inserts:
- **`@Headers("Prefer: return=minimal, resolution=merge-duplicates")`** — transforma o insert
  em **upsert**: se o título já existe, ele atualiza em vez de dar erro de duplicata (409).
- **`?on_conflict=user_id,tmdb_id,tipo`** — diz ao Postgres em qual chave única resolver o
  conflito (a mesma `UNIQUE` que existe no banco).
Em todos, o token vai no header `Authorization` (`@Header`) e o RLS garante o isolamento.

#### `TraktService.java` — busca e elenco na Trakt
- **Busca:** `GET /search/{type}?query=...&extended=full,images`. O `type` pode ser `movie`,
  `show` ou `movie,show` (ambos); o `extended=full,images` pede sinopse/nota e pôster.
  Devolve `List<TraktBusca>`.
- **Elenco:** `GET /movies/{id}/people` ou `GET /shows/{id}/people`. Devolve `TraktPessoas`.

#### `TokenAuthenticator.java` — renovação automática
Implementa o `Authenticator` do OkHttp. **Quando uma chamada às tabelas volta 401** (token
expirado), este código entra em ação automaticamente:
1. Confere um marcador para não entrar em loop infinito.
2. Pega o `refresh_token` salvo.
3. Chama o endpoint de refresh **de forma síncrona** (já está numa thread de rede).
4. Se der certo, salva o novo token na sessão e **repete a requisição original** com ele.
5. Se falhar, limpa a sessão e desiste.
O usuário nem percebe — é o que impede o app de "começar a dar erro depois de um tempo".

---

### 6.3 `controller` (regras e orquestração)

#### `SessionController.java` — a sessão segura
Guarda os dados da sessão **criptografados em disco** com `EncryptedSharedPreferences`
(a chave de criptografia fica no Android Keystore). Métodos:
- `salvarSessao(access, refresh, email, userId, expiresIn)` — grava tudo e calcula quando
  o token expira.
- `getAccessToken()`, `getRefreshToken()`, `getEmail()`, `getUserId()` — leitura.
- `estaLogado()` — existe token salvo? (usado no auto-login).
- `tokenExpirado()` — já passou da validade?
- `limpar()` — apaga tudo (logout).
Se o Keystore falhar (raro), há um *fallback* para prefs normais, só para o app não quebrar.

#### `ListaCallback.java`
Interface de retorno usada pelos três controllers de lista:
`onSucesso(List<ItemSalvo> itens)` e `onErro(String mensagem)`.

#### `AuthController.java` — autenticação
Concentra **toda** a autenticação. As telas só falam com ele.
- **`cadastrar(nome, email, senha, callback)`** — monta o `SignUpRequest` e chama o Supabase.
  No sucesso, se já vier sessão (confirmação de e-mail desligada), guarda o token.
- **`login(email, senha, callback)`** — envia as credenciais; no sucesso, guarda a sessão.
- **`logout(callback)`** — limpa a sessão local **primeiro** (o usuário sai mesmo sem rede)
  e avisa o Supabase.
- **`estaLogado()`** — atalho para o Splash decidir a tela inicial.
- **`guardarSessao(data)`** *(privado)* — salva access/refresh token, email e **user_id**
  (o user_id é necessário para o RLS aceitar os inserts nas listas).
- **`traduzirErro(...)`** *(privado)* — lê o corpo de erro do Supabase e devolve uma mensagem
  amigável (ex.: "E-mail ou senha incorretos").

#### `TraktController.java` — busca e elenco
- **`buscar(termo, filtro, callback)`** — escolhe o `type` conforme o filtro
  (Filme/Série/Ambos), chama a Trakt e, no sucesso, **converte** a resposta para
  `List<Titulo>` (o modelo que a tela e o adapter já entendem).
- **`buscarElenco(traktId, tipo, callback)`** — busca o elenco na Trakt
  (`/movies/{id}/people` ou `/shows/{id}/people`) e devolve a lista de nomes dos atores (UC04).
- **`tipoDoFiltro(...)`** / **`converter(...)`** *(privados)* — traduzem o filtro para o `type`
  da Trakt e transformam cada `TraktBusca` em `Titulo` (título, ano, nota, pôster, tipo).

#### `FavoritosController.java` (exemplo completo)
- **`listar(callback)`** — `GET` na tabela `favoritos`; devolve a lista pela callback.
- **`adicionar(item, callback)`** — preenche `item.userId` com o id da sessão (o RLS exige)
  e faz o `POST` (que é upsert).
- **`remover(item, callback)`** — `DELETE` filtrando por `tmdb_id` e `tipo` (UC09).

Os controllers de **Assistindo** e **Watchlist** seguem exatamente o mesmo formato, trocando só
o endpoint (`listarAssistindo`/`inserirAssistindo`/`removerAssistindo` e os equivalentes da
Watchlist).

---

### 6.4 `adapter` (RecyclerView)

Um *Adapter* liga uma lista de dados às "linhas" visuais da `RecyclerView`. Usa o padrão
**ViewHolder** (guarda as referências das views de cada item para reaproveitar e ficar rápido).

- **`TituloAdapter.java`** — mostra os **resultados de busca** (pôster com Glide, título, ano,
  nota). Tem uma interface `OnItemClickListener` que avisa a tela quando o usuário toca num
  item (a tela então abre os Detalhes). `atualizar(novos)` troca a lista exibida.
- **`ItemSalvoAdapter.java`** — mostra os itens das **listas salvas** (pôster, título e o tipo
  "Filme/Série"). Tem `atualizar(...)` e uma interface `OnRemover` que avisa a tela quando o
  usuário **segura (long-press)** um item, para removê-lo.

---

### 6.5 Activities (telas)

Cada Activity cuida **só da interface**: liga as views (`findViewById`), reage a cliques e
chama o controller certo. A lógica de rede mora nos controllers.

- **`SplashActivity`** — tela inicial. Anima a marca (logo com *bounce*, textos em fade
  sequencial) e, após ~1,4s, vai com *crossfade* para o **Menu** (se há sessão salva) ou
  para o **Login** (auto-login).
- **`LoginActivity`** — valida e-mail/senha localmente, chama `AuthController.login(...)`,
  mostra "Entrando..." enquanto carrega e vai para o Menu no sucesso.
- **`SignupActivity`** — valida (nome, e-mail, senha ≥ 6, confirmação), chama
  `AuthController.cadastrar(...)` e vai direto ao Menu (sem confirmação de e-mail).
- **`MenuActivity`** — o *hub*. Mostra o e-mail logado e tem botões para **Buscar**,
  **Favoritos**, **Assistindo**, **Watchlist** e **Sair**. O conteúdo entra com animação
  de fade + subida. O **logout existe só aqui** — assim a navegação tem um caminho único.
- **`MainActivity` (Busca)** — header com **botão de voltar** (retorna ao Menu) + campo de
  pesquisa + `RecyclerView`. Usa o `TraktController` e o `TituloAdapter`; também busca pela
  ação "pesquisar" do teclado. Ao tocar num resultado, abre os Detalhes mandando o `Titulo`
  via `Intent`.
- **`DetalhesActivity`** — header com **botão de voltar**; recebe o `Titulo`, mostra pôster
  (Glide, com cantos arredondados e *placeholder* de vidro), título, ano, nota, sinopse
  (`preencherDados`) e o **elenco** (`carregarElenco`, via Trakt). Tem três botões que
  salvam nas listas (`favoritar`, `marcarAssistindo`, `marcarWatchlist`) e, ao abrir, consulta
  as listas para **marcar os botões já salvos** (`verificarListas`). O `montarItem()` cria o
  `ItemSalvo` a partir do título para não repetir código.
- **`FavoritosActivity` / `AssistindoActivity` / `WatchlistActivity`** — header com **botão de
  voltar**; cada uma cria seu controller, monta a `RecyclerView` com o `ItemSalvoAdapter` e
  chama `listar(...)` no **`onResume`** (a lista sempre reflete o estado atual ao voltar).
  Mostra "Carregando…", a lista (com entrada em cascata), ou o estado vazio com dica.
  **Segurar um item** abre um diálogo de confirmação e remove (UC09).
- **`Ui.java`** — utilitário que aplica os *insets* das barras do sistema somando-os ao
  padding original da tela (edge-to-edge sem conteúdo embaixo da status bar).

---

### 6.6 Interface (Liquid Glass)

O visual segue a estética **liquid glass**: fundo navy cinematográfico com brilhos
(`bg_cinematic`), painéis e cartões translúcidos com borda clara (`glass_panel`,
`glass_input`), botões dourados com brilho (`glass_button`) e ripple de toque em todos os
elementos clicáveis (`glass_input_ripple`, `glass_circle`).

- **Tema (`values/themes.xml`)** — o app é **sempre escuro** (o tema noturno herda o mesmo
  estilo, então o modo claro do sistema não quebra o visual). A status bar e a barra de
  navegação são transparentes e o fundo cinematográfico já vem na janela (sem flash branco
  ao abrir telas).
- **Transições de tela** — definidas no tema (`windowAnimationStyle`): a tela nova desliza
  da direita com fade; ao voltar, a animação é invertida (`res/anim/slide_*`).
- **Listas** — os itens entram em cascata (`layout_fall_down` + `scheduleLayoutAnimation`).
- **Navegação** — todas as telas internas têm um botão circular de vidro para **voltar**;
  o botão físico/gesto de voltar do Android funciona igual.

---

## 7. Fluxos completos (passo a passo)

### Cadastro / Login
1. Usuário preenche o formulário → a Activity valida localmente.
2. `AuthController` chama o Supabase (`SupabaseService`).
3. O Supabase devolve `access_token` + `refresh_token` + `user`.
4. `SessionController` guarda tudo **criptografado** (incluindo o `user_id`).
5. App vai para o **Menu**.

### Busca (UC03)
1. Usuário digita e toca em **Buscar**.
2. `TraktController.buscar(...)` chama `GET /search/...` na Trakt.
3. A resposta (`List<TraktBusca>`) é convertida para `List<Titulo>`.
4. O `TituloAdapter` mostra os resultados; tocar abre os **Detalhes**.

### Favoritar / Assistindo / Watchlist (UC05–07)
1. Na tela de Detalhes, o usuário toca num botão.
2. A Activity monta um `ItemSalvo` (`montarItem`) e chama o controller.
3. O controller preenche `user_id`, manda o `POST` (upsert) com o token.
4. O Supabase valida pelo **RLS** e grava. O gatilho `mover_entre_listas` mantém
   Watchlist e Assistindo exclusivas. Um `Toast` confirma.

### Abrir os Detalhes (UC04)
1. A tela mostra pôster, título, ano, nota e sinopse com os dados recebidos.
2. Em paralelo, busca o **elenco** na Trakt e consulta as 3 listas para **marcar** os botões
   de títulos que já estão salvos (ex.: "✓ Favorito").

### Remover de uma lista (UC09)
1. Numa lista, o usuário **segura** um item.
2. Aparece um diálogo "Remover?". Ao confirmar, o controller faz o `DELETE` no Supabase e a
   lista é recarregada.

### Token expirado (renovação automática)
1. Depois de ~1h o token expira; uma chamada às tabelas volta **401**.
2. O `TokenAuthenticator` intercepta, renova com o `refresh_token`, salva o novo token e
   **repete** a requisição. O usuário não percebe nada.

---

## 8. Segurança

- **Token nunca em texto claro:** `EncryptedSharedPreferences` (Android Keystore).
- **Isolamento por usuário:** RLS no Supabase (`auth.uid() = user_id`) — a chave pública do
  cliente não dá acesso aos dados de outros.
- **Renovação de sessão:** feita automaticamente (sem pedir login de novo).
- **Chave secreta:** **não** fica no app — só a *publishable*. O Client Secret da Trakt
  também não é usado (a busca usa só o Client ID).

---

## 9. Usuários de teste

Contas já cadastradas para login (senha igual para todas: `cineglow123`):

| Nome        | E-mail                    | Senha       |
|-------------|---------------------------|-------------|
| Ana Souza   | ana.souza@cineglow.com    | cineglow123 |
| Bruno Lima  | bruno.lima@cineglow.com   | cineglow123 |
| Carla Dias  | carla.dias@cineglow.com   | cineglow123 |
| Diego Melo  | diego.melo@cineglow.com   | cineglow123 |
| Erica Nunes | erica.nunes@cineglow.com  | cineglow123 |

---

## 10. O que ainda é evolução futura

- **Recuperação de senha** — o link "Esqueceu a senha?" hoje só exibe um aviso; falta o
  fluxo de reset por e-mail do Supabase.
- **Progresso de séries** — o banco já tem `temporada_atual`/`episodio_atual` na tabela
  `assistindo`, mas o app ainda não edita esses campos.
- **Filtro de busca na interface** — o `TraktController` aceita Filme/Série/Ambos, porém a
  tela de Busca usa sempre "Ambos"; faltam os botões de filtro.
- **Paginação** — busca e listas carregam tudo de uma vez; com muitos resultados valeria
  paginar.
- **Testes** — só existem os testes de exemplo; faltam testes unitários dos controllers e
  de interface (Espresso).
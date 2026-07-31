<div align="center">

# 🎬 CINEGLOW

**Catálogo de filmes e séries para Android — busca, detalhes e listas pessoais.**

![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-100%25-ED8B00?logo=openjdk&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-Auth%20%2B%20Postgres-3ECF8E?logo=supabase&logoColor=white)
![Trakt](https://img.shields.io/badge/Trakt-API-ED1C24?logo=trakt&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit-2-48B983)

</div>

---

## O que é

Aplicativo Android nativo em Java para pesquisar filmes e séries, ver detalhes completos (sinopse, nota, elenco) e organizar títulos em três listas pessoais: **Favoritos**, **Assistindo** e **Watchlist**.

A autenticação e a persistência ficam no **Supabase** (Auth + PostgreSQL com Row Level Security); os dados de filmes e séries vêm da **API da Trakt**.

> **Projeto acadêmico** — avaliação de Mobile, Fatec Mococa.
> O código foi escrito para ser **fácil de ler**, com responsabilidades separadas em *activities*, *controllers*, *services* e *models*.

### Destaques técnicos

| | |
|---|---|
| 🧱 **Arquitetura em camadas** | A tela nunca fala com a rede. Activity → Controller → Network → API. |
| 🔄 **Duas APIs REST integradas** | Trakt (busca, elenco) e Supabase PostgREST (listas do usuário). |
| 🔐 **Sessão criptografada** | Token em `EncryptedSharedPreferences`, chave no Android Keystore. |
| ♻️ **Refresh automático de token** | `Authenticator` do OkHttp intercepta o 401, renova e repete a requisição. |
| 🛡️ **Isolamento por usuário no banco** | RLS com `auth.uid() = user_id` — a chave pública não vaza dados de terceiros. |
| ⚡ **Upsert idempotente** | `UNIQUE(user_id, tmdb_id, tipo)` + `Prefer: resolution=merge-duplicates`. |
| 🌌 **UI autoral "liquid glass"** | Tema escuro cinematográfico, painéis translúcidos, animações em cascata. |

---

---

## Sumário

1. [Stack](#1-stack)
2. [Arquitetura](#2-arquitetura)
3. [Estrutura de pastas](#3-estrutura-de-pastas)
4. [Como rodar](#4-como-rodar)
5. [Banco de dados](#5-banco-de-dados)
6. [Referência do código](#6-referência-do-código)
7. [Fluxos principais](#7-fluxos-principais)
8. [Segurança](#8-segurança)
9. [Roadmap](#9-roadmap)
10. [Equipe e licença](#10-equipe-e-licença)

---

## 1. Stack

| Camada | Tecnologia |
|---|---|
| Plataforma | Android nativo — `minSdk 24` (Android 7.0+) |
| Linguagem | Java |
| IDE / Build | Android Studio · Gradle (Kotlin DSL) |
| Auth + Banco | Supabase — GoTrue, PostgreSQL, RLS |
| API de conteúdo | Trakt · `https://api.trakt.tv` |
| HTTP | Retrofit 2 · OkHttp · Gson |
| Imagens | Glide |
| Listas | RecyclerView + ViewHolder |
| Sessão | EncryptedSharedPreferences (Android Keystore) |

---

## 2. Arquitetura

```
┌──────────────────────────────────────────────────────────────┐
│  ACTIVITIES — só interface e cliques                         │
│  Splash · Login · Signup · Menu · Busca · Detalhes           │
│  Favoritos · Assistindo · Watchlist                          │
└───────────────┬──────────────────────────────────────────────┘
                │ chamam
┌───────────────▼──────────────────────────────────────────────┐
│  CONTROLLERS — regras e orquestração                         │
│  Auth · Trakt · Favoritos · Assistindo · Watchlist · Session │
│  montam requisições e devolvem o resultado por callback      │
└───────────────┬──────────────────────────────────────────────┘
                │ usam
┌───────────────▼──────────────────────────────────────────────┐
│  NETWORK — Retrofit                                          │
│  ApiClient · SupabaseService · SupabaseDataService           │
│  TraktService · TokenAuthenticator                           │
└───────────────┬──────────────────────────────────────────────┘
                │ HTTP
┌───────────────▼──────────────────────────────────────────────┐
│  EXTERNO — Supabase · Trakt                                  │
└──────────────────────────────────────────────────────────────┘

MODELS (DTOs) atravessam todas as camadas:
AuthModels · Titulo · TraktBusca · TraktPessoas · ItemSalvo
```

**Comunicação assíncrona.** Chamadas de rede não podem travar a UI, então todo Controller recebe um **callback** com `onSucesso(...)` e `onErro(...)`. O Retrofit executa em background via `enqueue(...)` e devolve o resultado na main thread, onde a tela atualiza com segurança.

**Regra que sustenta tudo:** a Activity não conhece Retrofit, não conhece URL e não conhece token. Ela conhece um Controller.

---

## 3. Estrutura de pastas

```
app/src/main/
├── java/com/example/bgl/
│   ├── SplashActivity.java        # logado → Menu, senão → Login
│   ├── LoginActivity.java
│   ├── SignupActivity.java
│   ├── MenuActivity.java          # hub de navegação
│   ├── MainActivity.java          # busca (Trakt + RecyclerView)
│   ├── DetalhesActivity.java      # detalhes + botões de salvar
│   ├── FavoritosActivity.java
│   ├── AssistindoActivity.java
│   ├── WatchlistActivity.java
│   ├── Ui.java                    # helper de edge-to-edge (insets)
│   │
│   ├── adapter/
│   │   ├── TituloAdapter.java     # resultados de busca
│   │   └── ItemSalvoAdapter.java  # itens das listas salvas
│   │
│   ├── controller/
│   │   ├── SessionController.java # token criptografado
│   │   ├── AuthController.java    # login / cadastro / logout
│   │   ├── TraktController.java   # busca e elenco
│   │   ├── FavoritosController.java
│   │   ├── AssistindoController.java
│   │   ├── WatchlistController.java
│   │   └── ListaCallback.java     # callback compartilhado
│   │
│   ├── model/
│   │   ├── AuthModels.java
│   │   ├── Titulo.java
│   │   ├── TraktBusca.java
│   │   ├── TraktPessoas.java
│   │   └── ItemSalvo.java
│   │
│   └── network/
│       ├── ApiClient.java             # fábrica dos clientes Retrofit
│       ├── SupabaseService.java       # endpoints de Auth
│       ├── SupabaseDataService.java   # endpoints das tabelas
│       ├── TraktService.java
│       └── TokenAuthenticator.java    # renova o token no 401
│
├── res/layout/    # telas e itens de lista
├── res/drawable/  # fundo cinematográfico, vidro, ícones, logo
├── res/anim/      # transições e cascata das listas
├── res/values/    # cores, strings e tema (sempre escuro)
└── AndroidManifest.xml
```

---

## 4. Como rodar

### Pré-requisitos

- Android Studio (Ladybug ou superior) · JDK 17
- Um projeto no [Supabase](https://supabase.com)
- Um Client ID em [trakt.tv/oauth/applications](https://trakt.tv/oauth/applications)

### 4.1 Configurar as chaves

As chaves entram como `buildConfigField` em `app/build.gradle.kts` e ficam acessíveis via `BuildConfig`:

```kotlin
buildConfigField("String", "SUPABASE_URL", "\"https://SEU-PROJETO.supabase.co\"")
buildConfigField("String", "SUPABASE_KEY", "\"sb_publishable_...\"")
buildConfigField("String", "TRAKT_CLIENT_ID", "\"SEU_CLIENT_ID\"")
```

- **`SUPABASE_KEY`** é a chave *publishable* — pública por design. Quem protege os dados é o **RLS**, não o segredo da chave.
- **`TRAKT_CLIENT_ID`** basta o Client ID; o Client Secret não é usado (a busca não exige OAuth de usuário).

> 💡 **Recomendado:** mova os valores para `local.properties` (já ignorado pelo Git) e leia-os no Gradle, para não versionar chaves de projeto:
> ```kotlin
> val props = Properties().apply { load(rootProject.file("local.properties").inputStream()) }
> buildConfigField("String", "SUPABASE_URL", "\"${props["SUPABASE_URL"]}\"")
> ```

### 4.2 Criar o banco

Rode o SQL da [seção 5](#5-banco-de-dados) no SQL Editor do Supabase.

### 4.3 Executar

Abra o projeto no Android Studio → **Sync Project with Gradle Files** → **Run**.

---

## 5. Banco de dados

### Tabelas

| Tabela | Papel |
|---|---|
| `profiles` | Perfil do usuário (`id` referencia `auth.users`, `email`, `nome`). |
| `favoritos` | Lista de favoritos. |
| `assistindo` | Em andamento — tem `temporada_atual` e `episodio_atual`. |
| `watchlist` | Assistir mais tarde. |

Colunas comuns das três listas: `user_id`, `tmdb_id` (guarda o id da Trakt), `tipo` (`'movie'` \| `'tv'`), `titulo`, `poster_url`, `created_at`, `updated_at`.

### Triggers

| Trigger | O que faz |
|---|---|
| `set_updated_at` | Atualiza `updated_at` automaticamente a cada `UPDATE`. |
| `mover_entre_listas` | Ao inserir em `assistindo`, remove o título de `watchlist` — e vice-versa. Garante exclusividade mútua sem lógica no app. |

### Row Level Security

Todas as tabelas têm RLS ligado com política `auth.uid() = user_id`. Ou seja: **cada usuário só enxerga e altera as próprias linhas**, mesmo todos usando a mesma chave pública no cliente.

A constraint `UNIQUE(user_id, tmdb_id, tipo)` impede duplicatas e é exatamente o que viabiliza o *upsert* do app (ver `SupabaseDataService`).

<details>
<summary><b>SQL de referência (clique para expandir)</b></summary>

```sql
-- Exemplo para a tabela favoritos; assistindo e watchlist seguem o mesmo padrão.
create table public.favoritos (
  id          bigint generated always as identity primary key,
  user_id     uuid not null references auth.users(id) on delete cascade,
  tmdb_id     bigint not null,
  tipo        text   not null check (tipo in ('movie','tv')),
  titulo      text   not null,
  poster_url  text,
  created_at  timestamptz not null default now(),
  updated_at  timestamptz not null default now(),
  unique (user_id, tmdb_id, tipo)
);

alter table public.favoritos enable row level security;

create policy "dono lê"     on public.favoritos for select using (auth.uid() = user_id);
create policy "dono grava"  on public.favoritos for insert with check (auth.uid() = user_id);
create policy "dono altera" on public.favoritos for update using (auth.uid() = user_id);
create policy "dono apaga"  on public.favoritos for delete using (auth.uid() = user_id);
```

</details>

---

## 6. Referência do código

<details>
<summary><b>6.1 <code>model</code> — DTOs</b></summary>

O **Gson** preenche os campos a partir do JSON. Quando o nome no JSON difere do nome em Java, usamos `@SerializedName`.

**`AuthModels.java`** — agrupa os DTOs de autenticação:
- `SignUpRequest` — `email`, `password` e um objeto `data` com o `nome` (vai como *user metadata* e preenche o perfil).
- `LoginRequest` — `email` + `password`.
- `RefreshRequest` — `refresh_token`.
- `AuthResponse` — `access_token`, `refresh_token`, `expires_in`, `user`.
- `User` — `id` (uuid) e `email`.

**`Titulo.java`** — filme ou série vindo da busca. A Trakt usa `title` para filme e `name` para série; a classe esconde isso:
- `getTituloExibicao()` — devolve o nome correto.
- `getAno()` — extrai os 4 primeiros dígitos da data de lançamento.
- `getTipo()` — `"movie"` ou `"tv"` (converte o `"show"` da Trakt para o valor aceito pelo banco).
- `getPosterUrl()` — normaliza a URL (a Trakt manda o caminho sem `https://`).
- Implementa `Serializable` para trafegar entre telas via `Intent`.

**`TraktBusca.java`** — formato cru da resposta de busca. Cada item tem `type` e o objeto correspondente preenchido. Classes internas: `TraktItem`, `Ids`, `Images`. O método `getItem()` devolve o objeto certo; a conversão para `Titulo` acontece no `TraktController`.

**`ItemSalvo.java`** — título salvo em uma lista, com `@SerializedName` batendo com as colunas. Helper `getTipoAmigavel()` devolve "Filme" ou "Série".

**`TraktPessoas.java`** — resposta do endpoint de elenco. Usamos `cast[].person.name`.

</details>

<details>
<summary><b>6.2 <code>network</code> — camada de rede</b></summary>

**`ApiClient.java`** — fábrica singleton dos clientes Retrofit.
- `getSupabase()` — cliente de **Auth**. Interceptor injeta `apikey` e `Content-Type`. **Sem** renovação de token, para evitar loop quando o próprio refresh é chamado.
- `getSupabaseData()` — cliente das **tabelas**, com `TokenAuthenticator` acoplado.
- `getTrakt()` — injeta `trakt-api-key` e `trakt-api-version: 2`.
- `init(Context)` — guarda a `SessionController` que o `TokenAuthenticator` usa.
- `supabaseClientBuilder()` / `logging()` — helpers; o log HTTP só liga em DEBUG.

**`SupabaseService.java`** — endpoints do GoTrue:

| Método | Endpoint |
|---|---|
| `cadastrar(...)` | `POST /auth/v1/signup` |
| `login(...)` | `POST /auth/v1/token?grant_type=password` |
| `renovar(...)` | `POST /auth/v1/token?grant_type=refresh_token` |
| `logout(...)` | `POST /auth/v1/logout` |

**`SupabaseDataService.java`** — tabelas via PostgREST. Para cada lista há `GET` (listar), `POST` (inserir) e `DELETE` (filtrando `tmdb_id` e `tipo`). Dois detalhes importantes:
- `@Headers("Prefer: return=minimal, resolution=merge-duplicates")` transforma o insert em **upsert** — título repetido atualiza em vez de estourar 409.
- `?on_conflict=user_id,tmdb_id,tipo` indica ao Postgres qual chave única resolve o conflito.

O token vai no header `Authorization` e o RLS garante o isolamento.

**`TraktService.java`**
- Busca: `GET /search/{type}?query=...&extended=full,images` — `type` aceita `movie`, `show` ou `movie,show`.
- Elenco: `GET /movies/{id}/people` ou `GET /shows/{id}/people`.

**`TokenAuthenticator.java`** — implementa o `Authenticator` do OkHttp. Quando uma chamada volta **401**:
1. Confere um marcador para não entrar em loop.
2. Pega o `refresh_token` salvo.
3. Chama o refresh de forma síncrona (já está numa thread de rede).
4. Salva o novo token e **repete a requisição original**.
5. Se falhar, limpa a sessão.

O usuário não percebe nada — é o que impede o app de "começar a dar erro depois de um tempo".

</details>

<details>
<summary><b>6.3 <code>controller</code> — regras e orquestração</b></summary>

**`SessionController.java`** — sessão criptografada em disco com `EncryptedSharedPreferences` (chave no Android Keystore). Métodos: `salvarSessao(...)`, `getAccessToken()`, `getRefreshToken()`, `getEmail()`, `getUserId()`, `estaLogado()`, `tokenExpirado()`, `limpar()`. Se o Keystore falhar, há fallback para prefs normais só para o app não quebrar.

**`ListaCallback.java`** — `onSucesso(List<ItemSalvo>)` e `onErro(String)`, compartilhado pelos três controllers de lista.

**`AuthController.java`** — concentra toda a autenticação; as telas só falam com ele.
- `cadastrar(nome, email, senha, cb)` · `login(email, senha, cb)` · `logout(cb)` — o logout limpa a sessão local **primeiro**, então funciona mesmo sem rede.
- `estaLogado()` — atalho usado pelo Splash.
- `guardarSessao(data)` *(privado)* — salva tokens, email e **user_id** (necessário para o RLS aceitar os inserts).
- `traduzirErro(...)` *(privado)* — converte o erro do Supabase em mensagem amigável.

**`TraktController.java`**
- `buscar(termo, filtro, cb)` — escolhe o `type` conforme o filtro e converte a resposta para `List<Titulo>`.
- `buscarElenco(traktId, tipo, cb)` — devolve os nomes do elenco.
- `tipoDoFiltro(...)` / `converter(...)` *(privados)*.

**`FavoritosController.java`** (o padrão dos três)
- `listar(cb)` — `GET` na tabela.
- `adicionar(item, cb)` — preenche `item.userId` e faz o `POST` (upsert).
- `remover(item, cb)` — `DELETE` filtrando `tmdb_id` e `tipo`.

`AssistindoController` e `WatchlistController` são idênticos, trocando só o endpoint.

</details>

<details>
<summary><b>6.4 <code>adapter</code> — RecyclerView</b></summary>

Ambos usam o padrão **ViewHolder**, que guarda as referências das views de cada item para reaproveitá-las na rolagem.

- **`TituloAdapter`** — resultados de busca (pôster via Glide, título, ano, nota). Interface `OnItemClickListener` avisa a tela no toque; `atualizar(novos)` troca a lista.
- **`ItemSalvoAdapter`** — itens salvos (pôster, título, "Filme/Série"). Interface `OnRemover` dispara no **long-press**.

</details>

<details>
<summary><b>6.5 Activities</b></summary>

Cada Activity cuida **só da interface**: liga views, reage a cliques e chama o controller certo.

- **`SplashActivity`** — anima a marca (logo com bounce, textos em fade sequencial) e após ~1,4s faz crossfade para o Menu (sessão salva) ou Login.
- **`LoginActivity`** — valida localmente, chama `AuthController.login(...)`, mostra estado de carregamento.
- **`SignupActivity`** — valida nome, e-mail, senha ≥ 6 e confirmação; vai direto ao Menu.
- **`MenuActivity`** — hub. Mostra o e-mail logado e leva às quatro telas. O **logout existe só aqui**, para a navegação ter um caminho único.
- **`MainActivity` (Busca)** — campo de pesquisa + RecyclerView; também dispara pela ação "pesquisar" do teclado. Ao tocar num resultado, manda o `Titulo` via `Intent`.
- **`DetalhesActivity`** — pôster (Glide, cantos arredondados, placeholder de vidro), título, ano, nota, sinopse e **elenco**. Três botões de salvar; ao abrir, `verificarListas()` marca os que já estão salvos.
- **`FavoritosActivity` / `AssistindoActivity` / `WatchlistActivity`** — carregam no `onResume` (a lista sempre reflete o estado atual ao voltar). Estados: carregando, lista com entrada em cascata, ou vazio com dica. Long-press abre diálogo de confirmação e remove.
- **`Ui.java`** — aplica os insets das barras do sistema somando ao padding original (edge-to-edge sem conteúdo sob a status bar).

</details>

<details>
<summary><b>6.6 Interface — Liquid Glass</b></summary>

Fundo navy cinematográfico com brilhos (`bg_cinematic`), painéis e cartões translúcidos com borda clara (`glass_panel`, `glass_input`), botões dourados (`glass_button`) e ripple em todo elemento clicável (`glass_input_ripple`, `glass_circle`).

- **Tema** — o app é **sempre escuro**; o tema noturno herda o mesmo estilo, então o modo claro do sistema não quebra o visual. Status bar e navigation bar transparentes, com o fundo já na janela (sem flash branco ao abrir telas).
- **Transições** — definidas em `windowAnimationStyle`: a tela nova desliza da direita com fade; voltar inverte a animação.
- **Listas** — entrada em cascata (`layout_fall_down` + `scheduleLayoutAnimation`).
- **Navegação** — botão circular de vidro para voltar em todas as telas internas; o gesto nativo do Android funciona igual.

</details>

---

## 7. Fluxos principais

<details>
<summary><b>Cadastro e login</b></summary>

1. Activity valida o formulário localmente.
2. `AuthController` chama o Supabase.
3. Supabase devolve `access_token` + `refresh_token` + `user`.
4. `SessionController` grava tudo criptografado, incluindo o `user_id`.
5. App abre o Menu.

</details>

<details>
<summary><b>Busca (UC03)</b></summary>

1. Usuário digita e toca em Buscar.
2. `TraktController.buscar(...)` chama `GET /search/...`.
3. `List<TraktBusca>` vira `List<Titulo>`.
4. `TituloAdapter` renderiza; tocar abre os Detalhes.

</details>

<details>
<summary><b>Salvar em lista (UC05–07)</b></summary>

1. Toque num dos três botões em Detalhes.
2. `montarItem()` cria o `ItemSalvo` e chama o controller.
3. Controller preenche `user_id` e envia o `POST` (upsert) com o token.
4. RLS valida, grava, e o trigger `mover_entre_listas` mantém Watchlist e Assistindo exclusivas.

</details>

<details>
<summary><b>Detalhes (UC04)</b></summary>

1. Renderiza pôster, título, ano, nota e sinopse com os dados recebidos.
2. Em paralelo, busca o elenco na Trakt e consulta as três listas para marcar os botões já salvos.

</details>

<details>
<summary><b>Remover (UC09)</b></summary>

1. Long-press num item da lista.
2. Diálogo de confirmação → `DELETE` no Supabase → recarrega a lista.

</details>

<details>
<summary><b>Token expirado</b></summary>

1. Após ~1h o token expira e uma chamada volta 401.
2. `TokenAuthenticator` intercepta, renova com o `refresh_token`, salva e repete a requisição.

</details>

---

## 8. Segurança

| Risco | Como é tratado |
|---|---|
| Token em texto claro no dispositivo | `EncryptedSharedPreferences` com chave no Android Keystore. |
| Um usuário ler dados de outro | RLS no Postgres (`auth.uid() = user_id`) — a chave pública do cliente não dá acesso a linhas alheias. |
| Sessão expirando no meio do uso | Renovação automática via `TokenAuthenticator`, transparente ao usuário. |
| Segredo vazado no repositório | Só a chave *publishable* vai para o app. O Client Secret da Trakt não é usado. |

**Contas de teste:** existem contas de demonstração para avaliação. As credenciais não são versionadas — solicite ao mantenedor do repositório.

---

## 9. Roadmap

- [ ] Migrar as chaves para `local.properties` / `secrets-gradle-plugin`
- [ ] Cache offline das listas com Room, para abrir sem rede
- [ ] Paginação nos resultados de busca
- [ ] Controle de temporada e episódio na tela de Assistindo (as colunas já existem no banco)
- [ ] Testes unitários dos controllers e testes de instrumentação das telas
- [ ] Migração gradual para Kotlin + MVVM com ViewModel e LiveData
- [ ] Confirmação de e-mail no cadastro
- [ ] Modo claro opcional

---

## 10. Equipe e licença

**Desenvolvido por** Breno Bertucci · Letícia · Guilherme — Fatec Mococa, disciplina de Programação Mobile.

Este projeto usa a [API da Trakt](https://trakt.docs.apiary.io/) e não é afiliado à Trakt.

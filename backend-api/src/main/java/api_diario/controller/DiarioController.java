package api_diario.controller;

import api_diario.model.Diario;
import api_diario.repository.DiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diario")
public class DiarioController {

    // A injeção de dependência conecta o nosso Controller ao banco de dados.
    @Autowired
    private DiarioRepository repository;

    /**
     * Endpoint POST: Recebe o JSON do Android e salva/atualiza no banco H2.
     * Funciona como um "Upsert": se o traktId já existir, ele sobrescreve o texto.
     */
    @PostMapping
    public ResponseEntity<Void> salvarNota(@RequestBody Diario diario) {
        repository.save(diario);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint GET: O Android pede a nota de um filme específico.
     * Retorna 200 OK com o JSON se encontrar, ou 404 Not Found se o usuário ainda não tiver nota para este filme.
     */
    @GetMapping("/filme/{traktId}")
    public ResponseEntity<Diario> buscarNota(@PathVariable Integer traktId) {
        return repository.findById(traktId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

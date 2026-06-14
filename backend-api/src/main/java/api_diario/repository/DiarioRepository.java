package api_diario.repository;

import api_diario.model.Diario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiarioRepository extends JpaRepository<Diario, Integer> {
    // JpaRepository exige dois parâmetros: <A Classe da Entidade, O tipo da Chave Primária>.
    // Não precisa escrever nenhum método aqui. O Spring herda os métodos save(), findById(), delete() etc.
}

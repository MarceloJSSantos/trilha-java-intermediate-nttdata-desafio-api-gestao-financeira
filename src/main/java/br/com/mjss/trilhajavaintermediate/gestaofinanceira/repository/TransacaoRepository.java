package br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Transacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("SELECT saldo from Transacao where id = (select MAX(id) FROM Transacao where usuario = :usuario)")
    Optional<BigDecimal> findTopSaldoJQPL(Usuario usuario);
}
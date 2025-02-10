package br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TransacaoComSaldoView;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransacaoComSaldoViewRepository extends JpaRepository<TransacaoComSaldoView, Long> {
  Page<TransacaoComSaldoView> findAllByUsuarioAndDataHoraTransacaoBetween(Pageable paginacao, Usuario usuario, LocalDateTime dataInicial, LocalDateTime dataFinal);
  Optional<TransacaoComSaldoView> findTopByUsuarioAndDataHoraTransacaoBetweenOrderByDataHoraTransacaoDescIdDesc(Usuario usuario, LocalDateTime dataInicial, LocalDateTime dataFinal);
}
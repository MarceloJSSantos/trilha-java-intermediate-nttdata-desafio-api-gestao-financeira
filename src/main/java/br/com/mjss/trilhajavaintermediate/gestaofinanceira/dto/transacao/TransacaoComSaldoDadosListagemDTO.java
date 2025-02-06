package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoComSaldoDadosListagemDTO(
        Long id,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dataHoraTransacao,
        TipoTransacao tipo,
        Categoria categoria,
        String descricao,
        BigDecimal valor,
        Metodo metodo,
        Long usuarioId,
        BigDecimal saldo
) {
    public TransacaoComSaldoDadosListagemDTO(TransacaoComSaldoView transacaoComSaldo) {
        this(transacaoComSaldo.getId(), transacaoComSaldo.getDataHoraTransacao(), transacaoComSaldo.getTipo(),
                transacaoComSaldo.getCategoria(), transacaoComSaldo.getDescricao(), transacaoComSaldo.getValor(),
                transacaoComSaldo.getMetodo(), transacaoComSaldo.getUsuario().getId(), transacaoComSaldo.getSaldo());
    }
}

package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Metodo;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Transacao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoDadosAposCadastroOuAtualizacaoDTO(
        Long id,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dataHoraTransacao,
        TipoTransacao tipo,
        Categoria categoria,
        String descricao,
        BigDecimal valor,
        Metodo metodo,
        Long usuarioId
) {
    public TransacaoDadosAposCadastroOuAtualizacaoDTO(Transacao transacao) {
        this(transacao.getId(), transacao.getDataHoraTransacao(), transacao.getTipo(), transacao.getCategoria(),
                transacao.getDescricao(), transacao.getValor(), transacao.getMetodo(), transacao.getUsuario().getId());
    }
}

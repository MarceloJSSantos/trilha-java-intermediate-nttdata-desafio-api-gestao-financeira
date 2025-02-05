package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Metodo;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoAtualizacaoDTO(
        @NotNull
        Long id,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dataHoraTransacao,

        TipoTransacao tipo,

        Categoria categoria,

        String descricao,

        BigDecimal valor,

        Metodo metodo
) {
}

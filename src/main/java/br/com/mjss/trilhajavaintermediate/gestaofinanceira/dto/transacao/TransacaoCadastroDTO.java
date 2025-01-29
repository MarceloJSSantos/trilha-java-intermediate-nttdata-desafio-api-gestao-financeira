package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Metodo;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoCadastroDTO(
        @NotNull
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dataHoraTransacao,

        @NotNull
        TipoTransacao tipo,

        @NotNull
        Categoria categoria,

        String descricao,

        @NotNull
        BigDecimal valor,

        @NotNull
        Metodo metodo,

        @NotNull
        Long idUsuario
) {
}

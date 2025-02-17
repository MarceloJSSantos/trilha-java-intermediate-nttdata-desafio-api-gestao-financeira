package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Metodo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO {
    private Metodo metodo;
    @Setter
    private int quantidadeTransacoes;
    @Setter
    private BigDecimal totalValor;
}

package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO {
    private Categoria categoria;
    @Setter
    private int quantidadeTransacoes;
    @Setter
    private BigDecimal totalValor;
}

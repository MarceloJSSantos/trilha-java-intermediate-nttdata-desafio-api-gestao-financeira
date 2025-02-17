package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class ResumoTransacaoDeUsuarioPorPeriodoDTO {
    private String dataInicial;
    private String dataFinal;
    private Long idUsuario;
    private int quantidadeTransacoes;
    private BigDecimal totalValor;
    private List<ResumoTransacaoDeUsuarioPorPeriodoTipoDTO> tipos;
}

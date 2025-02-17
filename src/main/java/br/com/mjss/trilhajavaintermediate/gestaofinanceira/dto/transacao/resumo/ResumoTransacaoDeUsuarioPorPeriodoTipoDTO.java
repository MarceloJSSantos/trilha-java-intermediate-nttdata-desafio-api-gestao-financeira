package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ResumoTransacaoDeUsuarioPorPeriodoTipoDTO {
    private TipoTransacao tipo;
    @Setter
    private int quantidadeTransacoes;
    @Setter
    private BigDecimal totalValor;
    private List<ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO> categorias;
    private List<ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO> metodos;

    public ResumoTransacaoDeUsuarioPorPeriodoTipoDTO(TipoTransacao tipo,
                                                     int quantidadeTransacoesTipo,
                                                     BigDecimal totalValorTipo) {
        this.tipo = tipo;
        this.quantidadeTransacoes = quantidadeTransacoesTipo;
        this.totalValor = totalValorTipo;
        this.categorias = new ArrayList<>();
        this.metodos = new ArrayList<>();
    }

    public void adicionarCategoria(ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO categoriaDTO) {
        this.categorias.add(categoriaDTO);
    }

    public void adicionarMetodo(ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO metodoDTO) {
        this.metodos.add(metodoDTO);
    }
}

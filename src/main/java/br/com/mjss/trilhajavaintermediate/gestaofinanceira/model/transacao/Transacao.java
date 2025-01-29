package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "transacoes")
@Entity(name = "Transacao")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHoraTransacao;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    private String descricao;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private Metodo metodo;

    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    public Transacao(Usuario usuario, TransacaoCadastroDTO dto) {
        this.dataHoraTransacao = dto.dataHoraTransacao();
        this.tipo = dto.tipo();
        setCategoria(dto.categoria());
        this.descricao = dto.descricao();
        this.valor = dto.valor();
        setMetodo(dto.metodo());
        this.saldo = dto.valor();
        this.usuario = usuario;
    }

    private void setCategoria(Categoria categoria) {
        var seTipoTransacaoEDespesa = (this.tipo == TipoTransacao.DESPESA);
        var seTipoDaCategoriaNaoEDespesa = !categoria.getTipo().equals(TipoTransacao.DESPESA);

        var seTipoTransacaoEReceita = (this.tipo == TipoTransacao.RECEITA);
        var seTipoDaCategoriaNaoEReceita = !categoria.getTipo().equals(TipoTransacao.RECEITA);

        // Verifique se o tipo e a categoria são compatíveis
        if (seTipoTransacaoEDespesa && seTipoDaCategoriaNaoEDespesa) {
            throw new IllegalArgumentException("Categoria " + categoria + " é inválida para tipo DESPESA.");
        } else if (seTipoTransacaoEReceita && seTipoDaCategoriaNaoEReceita) {
            throw new IllegalArgumentException("Categoria " + categoria + " é inválida para tipo RECEITA.");
        }
        this.categoria = categoria;
    }

    private void setMetodo(Metodo metodo) {
        var seTipoTransacaoEDespesa = (this.tipo == TipoTransacao.DESPESA);
        var seTipoDoMetodoNaoEDespesa = !metodo.getTipo().equals(TipoTransacao.DESPESA);

        var seTipoTransacaoEReceita = (this.tipo == TipoTransacao.RECEITA);
        var seTipoDoMetodoNaoEReceita = !metodo.getTipo().equals(TipoTransacao.RECEITA);

        // Verifique se o tipo e o método são compatíveis
        if (seTipoTransacaoEDespesa && seTipoDoMetodoNaoEDespesa) {
            throw new IllegalArgumentException("Método " + metodo + " é inválido para tipo DESPESA.");
        } else if (seTipoTransacaoEReceita && seTipoDoMetodoNaoEReceita) {
            throw new IllegalArgumentException("Método " + metodo + " é inválido para tipo RECEITA.");
        }
        this.metodo = metodo;
    }

    public void setSaldo(BigDecimal saldoAnterior, BigDecimal valorAtual, TipoTransacao tipo){
        var seNaoTipoTransacaoReceitaEValorAtualPositivo = !(tipo.equals(TipoTransacao.RECEITA) && valorAtual.signum() == 1);
        if (seNaoTipoTransacaoReceitaEValorAtualPositivo){
            throw new IllegalArgumentException("O valor %f não é apropriado para o tipo de transação %s.".formatted(valorAtual, tipo));
        }
        this.saldo = saldoAnterior.add(valorAtual);
    }

}

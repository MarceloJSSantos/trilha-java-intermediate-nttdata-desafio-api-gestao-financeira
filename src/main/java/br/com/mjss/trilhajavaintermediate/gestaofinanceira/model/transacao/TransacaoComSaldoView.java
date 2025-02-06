package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "view_transacoes_com_saldo")
@Entity(name = "TransacaoComSaldoView")
@Getter
@EqualsAndHashCode(of = "id")
public class TransacaoComSaldoView {
    @Id
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    private BigDecimal saldo;

}

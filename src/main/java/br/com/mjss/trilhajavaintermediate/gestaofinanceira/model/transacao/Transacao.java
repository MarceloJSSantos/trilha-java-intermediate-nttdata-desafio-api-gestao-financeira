package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.utils.CategoriaConverter;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.utils.MetodoConverter;
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

    @Convert(converter = CategoriaConverter.class)
    private Categoria categoria;

    private String descricao;

    private BigDecimal valor;

    @Convert(converter = MetodoConverter.class)
    private Metodo metodo;

    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;
}

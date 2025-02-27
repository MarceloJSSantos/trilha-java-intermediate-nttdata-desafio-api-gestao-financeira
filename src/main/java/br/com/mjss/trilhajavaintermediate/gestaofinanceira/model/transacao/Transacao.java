package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoAtualizacaoDTO;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    public Transacao(Usuario usuario, TransacaoCadastroDTO dto) {
        this.dataHoraTransacao = dto.dataHoraTransacao();
        this.tipo = dto.tipo();
        this.categoria = dto.categoria();
        this.descricao = dto.descricao();
        this.valor = dto.valor();
        this.metodo = dto.metodo();
        this.usuario = usuario;
    }

    public void atualizarTransacao(TransacaoAtualizacaoDTO dto) {
        if(dto.dataHoraTransacao() != null) this.dataHoraTransacao = dto.dataHoraTransacao();
        if(dto.tipo() != null) this.tipo = dto.tipo();
        if(dto.categoria() != null) this.categoria = dto.categoria();
        if (dto.descricao() != null) this.descricao = dto.descricao();
        if (dto.valor() != null) this.valor = dto.valor();
        if (dto.metodo() != null) this.metodo = dto.metodo();
    }
}

package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioDadosParaAtualizacaoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String login;
    private String senha;
    private boolean ativo;

    public Usuario(UsuarioCadastroDTO dto) {
        this.nome = dto.nome();
        this.login = dto.login();
        this.senha = dto.senha();
        this.ativo = true;
    }

    public void atualizarDados(UsuarioDadosParaAtualizacaoDTO dto) {
        if(dto.nome() != null) this.nome = dto.nome();
        if(dto.login() != null) this.login = dto.login();
        if (dto.senha() != null) this.senha = dto.senha();
    }

    public void desativar() {
        this.ativo = false;
    }

    public void reativar() {
        this.ativo = true;
    }
}

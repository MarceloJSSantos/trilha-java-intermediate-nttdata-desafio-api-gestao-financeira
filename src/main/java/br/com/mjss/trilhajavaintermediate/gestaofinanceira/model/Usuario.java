package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioCadastroDTO;
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

    public Usuario(UsuarioCadastroDTO dto) {
        this.nome = dto.nome();
        this.login = dto.login();
        this.senha = dto.senha();
    }
}

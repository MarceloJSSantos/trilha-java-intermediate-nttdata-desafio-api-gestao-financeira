package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.usuario;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UsuarioDadosParaAtualizacaoDTO(
        @NotNull
        Long id,

        @Column(unique = true)
        String nome,

        @Email
        String login,

        String senha
) {
}

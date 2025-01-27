package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioCadastroDTO(
        @NotBlank
        String nome,

        @NotBlank
        @Email
        String login,

        @NotBlank
        String senha
) {
}

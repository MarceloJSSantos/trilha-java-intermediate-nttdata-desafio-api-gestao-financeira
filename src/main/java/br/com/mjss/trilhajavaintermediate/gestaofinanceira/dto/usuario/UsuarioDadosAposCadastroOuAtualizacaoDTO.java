package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.usuario;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;

public record UsuarioDadosAposCadastroOuAtualizacaoDTO(
        Long id,
        String nome,
        String login) {
    public UsuarioDadosAposCadastroOuAtualizacaoDTO(Usuario usuario){
        this(usuario.getId(), usuario.getNome(), usuario.getLogin());
    }
}

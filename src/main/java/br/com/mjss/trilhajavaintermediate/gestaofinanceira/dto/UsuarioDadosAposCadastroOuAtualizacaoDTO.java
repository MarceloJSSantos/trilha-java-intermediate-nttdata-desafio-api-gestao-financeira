package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.Usuario;

public record UsuarioDadosAposCadastroOuAtualizacaoDTO(
        Long id,
        String nome,
        String login,
        String senha) {
    public UsuarioDadosAposCadastroOuAtualizacaoDTO(Usuario usuario){
        this(usuario.getId(), usuario.getNome(), usuario.getLogin(), usuario.getSenha());
    }
}

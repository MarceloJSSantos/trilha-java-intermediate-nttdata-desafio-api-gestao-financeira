package br.com.mjss.trilhajavaintermediate.gestaofinanceira.service;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioDadosParaAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.reposiyory.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public Usuario cadastrar(UsuarioCadastroDTO dto){
        var usuario = new Usuario(dto);
        return repository.save(usuario);
    }

    public Usuario consultarUsuario(Long id){
        return repository.getReferenceById(id);
    }

    public Usuario atualizar(UsuarioDadosParaAtualizacaoDTO dto){
        var usuario = repository.getReferenceById(dto.id());
        usuario.atualizarDados(dto);
        return usuario;
    }

    public void desativar(Long id){
        var usuario = repository.getReferenceById(id);
        usuario.desativar();
    }
    public void reativar(Long id){
        var usuario = repository.getReferenceById(id);
        usuario.reativar();;
    }

}

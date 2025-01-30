package br.com.mjss.trilhajavaintermediate.gestaofinanceira.service;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.usuario.UsuarioCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.usuario.UsuarioDadosAposCadastroOuAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.usuario.UsuarioDadosParaAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public UsuarioDadosAposCadastroOuAtualizacaoDTO consultarUsuario(Long id){
        try{
            var usuario = repository.getReferenceById(id);
            var dto = new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario);
            return dto;
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("O usuário com ID '%s' não foi encontrado!".formatted(id));
        }
    }

    public UsuarioDadosAposCadastroOuAtualizacaoDTO atualizar(UsuarioDadosParaAtualizacaoDTO dto){
        try {
            var usuario = repository.getReferenceById(dto.id());
            usuario.atualizarDados(dto);
            var respostaDto = new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario);
            return respostaDto;
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("O usuário com ID '%s' não foi encontrado!".formatted(dto.id()));
        }
    }

    public UsuarioDadosAposCadastroOuAtualizacaoDTO desativar(Long id){
        try{
            var usuario = repository.getReferenceById(id);
            usuario.desativar();
            return new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario);
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("O usuário com ID '%s' não foi encontrado!".formatted(id));
        }
    }
    public UsuarioDadosAposCadastroOuAtualizacaoDTO reativar(Long id){
        try{
            var usuario = repository.getReferenceById(id);
            usuario.reativar();
            return new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario);
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("O usuário com ID '%s' não foi encontrado!".formatted(id));
        }
    }

}

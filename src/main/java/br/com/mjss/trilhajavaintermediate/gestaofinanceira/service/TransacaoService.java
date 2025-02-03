package br.com.mjss.trilhajavaintermediate.gestaofinanceira.service;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoDadosAposCadastroOuAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Transacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Transacao cadastrar(TransacaoCadastroDTO dto){
        var usuario = usuarioRepository.getReferenceById(dto.idUsuario());
        var transacao = new Transacao(usuario, dto);

        var seExisteUsuario = (usuarioRepository.existsById(dto.idUsuario()));

        if (!seExisteUsuario){
            throw new IllegalArgumentException("O ID %d não corresponde a nenhum usuário.".formatted(dto.idUsuario()));
        }

        transacao.validaSeSaldoAdequadoComTipo(dto.valor(), dto.tipo());

        return repository.save(transacao);
    }

    public TransacaoDadosAposCadastroOuAtualizacaoDTO consultarTransacao(Long id){
        try{
            var transacao = repository.getReferenceById(id);
            var dto = new TransacaoDadosAposCadastroOuAtualizacaoDTO(transacao);
            return dto;
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("A transação com ID '%s' não foi encontrado!".formatted(id));
        }
    }

    public void excluirTransacao(Long id){
        if (repository.existsById(id))
            repository.deleteById(id);
        else {
            throw new EntityNotFoundException("A transação com ID '%s' não foi encontrado!".formatted(id));
        }
    }
}


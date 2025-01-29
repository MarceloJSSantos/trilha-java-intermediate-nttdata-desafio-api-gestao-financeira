package br.com.mjss.trilhajavaintermediate.gestaofinanceira.controller;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Transacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid TransacaoCadastroDTO dto
//            , UriComponentsBuilder uriBuilder
            ){
//        var usuario = service.cadastrar(dto);
//        var uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
//        return ResponseEntity.created(uri).body(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
        var usuario = usuarioRepository.getReferenceById(dto.idUsuario());
        var transacao = new Transacao(usuario, dto);

        var resultado = repository.findTopSaldoJQPL(usuario);
        var seNaoExisteUsuario = !(repository.existsById(dto.idUsuario()));
        var seExisteSaldoAnterior = resultado.isPresent();
        var saldoAnterior = new BigDecimal(0);

        if (seNaoExisteUsuario){
            throw new IllegalArgumentException("O ID %d não corresponde a nenhum usuário.".formatted(dto.idUsuario()));
        }
        if(seExisteSaldoAnterior){
            saldoAnterior = resultado.get();
        }

        transacao.setSaldo(saldoAnterior, dto.valor(), dto.tipo());
        repository.save(transacao);
        return ResponseEntity.ok().build();
    }
}

package br.com.mjss.trilhajavaintermediate.gestaofinanceira.controller;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoDadosAposCadastroOuAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService service;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid TransacaoCadastroDTO dto, UriComponentsBuilder uriBuilder){
        var transacao = service.cadastrar(dto);
        var uri = uriBuilder.path("/transacoes/{id}").buildAndExpand(transacao.getId()).toUri();
        return ResponseEntity.created(uri).body(new TransacaoDadosAposCadastroOuAtualizacaoDTO(transacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity consultarTransacao(@PathVariable Long id){
        var respostaDTO = service.consultarTransacao(id);
        return ResponseEntity.ok(respostaDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluirTransacao(@PathVariable Long id){
        service.excluirTransacao(id);
        return ResponseEntity.noContent().build();
    }
}

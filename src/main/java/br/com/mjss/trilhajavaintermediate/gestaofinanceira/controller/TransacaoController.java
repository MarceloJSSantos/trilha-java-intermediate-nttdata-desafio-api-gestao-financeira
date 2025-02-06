package br.com.mjss.trilhajavaintermediate.gestaofinanceira.controller;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.*;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/listar/{usuarioId}")
    public ResponseEntity<Page<TransacaoDadosListagemDTO>> listarTransacoesDeUsuario(
            @RequestParam(defaultValue = "0") int page,  // Página padrão 0
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long usuarioId){
        Pageable paginacao = PageRequest.of(page, size, Sort.by("id").ascending());
        var respotaPaginada = service.listarTransacoesDeUsuario(paginacao, usuarioId);
        return ResponseEntity.ok(respotaPaginada);
    }

    @GetMapping("/listarcomsaldo/{usuarioId}")
    public ResponseEntity<Page<TransacaoComSaldoDadosListagemDTO>> listarTransacoesComSaldoDeUsuario(
            @RequestParam(defaultValue = "0") int page,  // Página padrão 0
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long usuarioId){
        Pageable paginacao = PageRequest.of(page, size, Sort.by("id").ascending());
        var respotaPaginada = service.listarTransacoesComSaldoDeUsuario(paginacao, usuarioId);
        return ResponseEntity.ok(respotaPaginada);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizarTransacao(@RequestBody @Valid TransacaoAtualizacaoDTO dto){
        var transacao = service.atualizarTransacao(dto);
        return ResponseEntity.ok(new TransacaoDadosAposCadastroOuAtualizacaoDTO(transacao));
    }
}

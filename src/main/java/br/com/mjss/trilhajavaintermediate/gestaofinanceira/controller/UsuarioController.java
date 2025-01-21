package br.com.mjss.trilhajavaintermediate.gestaofinanceira.controller;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioDadosAposCadastroOuAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioDadosParaAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid UsuarioCadastroDTO dto, UriComponentsBuilder uriBuilder){
        var usuario = service.cadastrar(dto);
        var uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity consultarUsuario(@PathVariable Long id){
        var usuario = service.consultarUsuario(id);
        return ResponseEntity.ok(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid UsuarioDadosParaAtualizacaoDTO dto){
        var usuario = service.atualizar(dto);
        return ResponseEntity.ok(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
    }

    @PutMapping("/{id}/desativar")
    @Transactional
    public ResponseEntity desativar(@PathVariable Long id){
        service.desativar(id);
        return ResponseEntity.ok("Usuário desativado!");
    }

    @PutMapping("/{id}/reativar")
    @Transactional
    public ResponseEntity reativar(@PathVariable Long id){
        service.reativar(id);
        return ResponseEntity.ok("Usuário reativado!");
    }
}

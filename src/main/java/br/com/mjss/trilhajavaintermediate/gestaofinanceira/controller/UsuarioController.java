package br.com.mjss.trilhajavaintermediate.gestaofinanceira.controller;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioDadosAposCadastroOuAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioDadosParaAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.reposiyory.UsuarioRepository;
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
    private UsuarioRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid UsuarioCadastroDTO dto, UriComponentsBuilder uriBuilder){
        var usuario = new Usuario(dto);
        repository.save(usuario);
        var uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity consultarUsuario(@PathVariable Long id){
        var usuario = repository.getReferenceById(id);
        return ResponseEntity.ok(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid UsuarioDadosParaAtualizacaoDTO dto){
        var usuario =repository.getReferenceById(dto.id());
        usuario.atualizarDados(dto);
        return ResponseEntity.ok(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
    }
}

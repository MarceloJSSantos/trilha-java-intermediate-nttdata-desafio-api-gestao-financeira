package br.com.mjss.trilhajavaintermediate.gestaofinanceira.controller;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioDadosAposCadastroOuAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.reposiyory.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid UsuarioCadastroDTO dto){
        var usuario = new Usuario(dto);
        repository.save(usuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity consultarUsuario(@PathVariable Long id){
        var usuario = repository.getReferenceById(id);
        return ResponseEntity.ok(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
    }
}

package br.com.mjss.trilhajavaintermediate.gestaofinanceira.controller;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioDadosAposCadastroOuAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.UsuarioDadosParaAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        try {
            var usuario = service.cadastrar(dto);
            var uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
            return ResponseEntity.created(uri).body(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
        } catch (RuntimeException e){
//            TODO: Implentar um Tratador de Erros para capturar a exception
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity consultarUsuario(@PathVariable Long id){
        try{
            var usuario = service.consultarUsuario(id);
            return ResponseEntity.ok(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
        } catch (EntityNotFoundException e){
            var mensagemDeErro = "O usuário com ID '%s' não foi encontrado!".formatted(id);
            return new ResponseEntity<>(mensagemDeErro, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid UsuarioDadosParaAtualizacaoDTO dto){
        try{
            var usuario = service.atualizar(dto);
            return ResponseEntity.ok(new UsuarioDadosAposCadastroOuAtualizacaoDTO(usuario));
        } catch (EntityNotFoundException e){
            var mensagemDeErro = "O usuário com ID '%s' não foi encontrado!".formatted(dto.id());
            return new ResponseEntity<>(mensagemDeErro, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/desativar")
    @Transactional
    public ResponseEntity desativar(@PathVariable Long id){
        try{
            service.desativar(id);
            return ResponseEntity.ok("Usuário desativado!");
        } catch (EntityNotFoundException e){
            var mensagemDeErro = "O usuário com ID '%s' não foi encontrado!".formatted(id);
            return new ResponseEntity<>(mensagemDeErro, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/reativar")
    @Transactional
    public ResponseEntity reativar(@PathVariable Long id){
        try{
            service.reativar(id);
            return ResponseEntity.ok("Usuário reativado!");
        } catch (EntityNotFoundException e){
            var mensagemDeErro = "O usuário com ID '%s' não foi encontrado!".formatted(id);
            return new ResponseEntity<>(mensagemDeErro, HttpStatus.NOT_FOUND);
        }
    }
}

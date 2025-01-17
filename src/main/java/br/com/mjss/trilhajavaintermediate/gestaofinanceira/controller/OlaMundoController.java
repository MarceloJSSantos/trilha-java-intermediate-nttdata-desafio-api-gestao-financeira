package br.com.mjss.trilhajavaintermediate.gestaofinanceira.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oi")
public class OlaMundoController {

    @GetMapping
    public String olaMundo(){
        return "Oi, seja bem vindo ao Sistema de Gestão Financeira Pessoal";
    }
}

package br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ResultadoDuploCategoriaEMensagem {
    private Categoria categoria;
    private List<String> motivos;
}

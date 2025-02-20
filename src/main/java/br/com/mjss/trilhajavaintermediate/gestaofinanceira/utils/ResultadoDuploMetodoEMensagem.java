package br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Metodo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResultadoDuploMetodoEMensagem {
    private Metodo metodo;
    private String mensagemMotivo;
}

package br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResultadoDuploTipoTransacaoEMensagem {
    private TipoTransacao tipoTransacao;
    private String mensagemMotivo;
}

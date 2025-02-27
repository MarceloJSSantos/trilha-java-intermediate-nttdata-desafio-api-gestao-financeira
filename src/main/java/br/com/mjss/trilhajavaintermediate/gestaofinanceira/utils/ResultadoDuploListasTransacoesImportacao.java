package br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.importacaoPlanilha.TransacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Transacao;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ResultadoDuploListasTransacoesImportacao {
    private List<Transacao> transacoesProcessadas;
    private List<TransacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO> transacoesNaoProcessadas;
}

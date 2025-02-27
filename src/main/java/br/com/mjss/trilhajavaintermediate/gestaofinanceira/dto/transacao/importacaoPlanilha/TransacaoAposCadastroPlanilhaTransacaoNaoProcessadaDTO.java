package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.importacaoPlanilha;

import java.util.List;

public record TransacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO(
        long linha,
        List<String> motivos
) {
    public TransacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO(long linha, List<String> motivos) {
        this.linha = linha;
        this.motivos = motivos;
    }
}

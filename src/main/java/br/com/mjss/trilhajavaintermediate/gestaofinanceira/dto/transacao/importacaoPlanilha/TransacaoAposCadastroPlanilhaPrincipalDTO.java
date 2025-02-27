package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.importacaoPlanilha;

import java.util.List;

public record TransacaoAposCadastroPlanilhaPrincipalDTO(
        String mensagem,
        long quantidadeTransacoesNaPlanilha,
        long quantidadeTransacoesProcessadas,
        long quantidadeTransacoescNaoProcessadas,
        List<TransacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO> transacoesNaoProcessdas
) {
    static final String MENSAGEM = "ATENÇÃO, caso haja transações 'Não processadas', crie uma nova planilha com as linhas acertadas e importe novamente.";
    public TransacaoAposCadastroPlanilhaPrincipalDTO(long quantidadeTransacoesNaPlanilha,
                                                     long quantidadeTransacoesProcessadas,
                                                     long quantidadeTransacoescNaoProcessadas,
                                                     List<TransacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO> transacoesNaoProcessdas) {
        this(MENSAGEM, quantidadeTransacoesNaPlanilha, quantidadeTransacoesProcessadas, quantidadeTransacoescNaoProcessadas, transacoesNaoProcessdas);
    }
}

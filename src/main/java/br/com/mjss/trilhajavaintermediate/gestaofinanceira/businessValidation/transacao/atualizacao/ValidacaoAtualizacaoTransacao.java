package br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.atualizacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoAtualizacaoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;

public interface ValidacaoAtualizacaoTransacao {
    void valida(TransacaoAtualizacaoDTO dto);
}

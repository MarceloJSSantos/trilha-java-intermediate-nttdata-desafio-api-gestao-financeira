package br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.cadastro;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;

public interface ValidacaoCadastroTransacao {
    void valida(TransacaoCadastroDTO dto);
}

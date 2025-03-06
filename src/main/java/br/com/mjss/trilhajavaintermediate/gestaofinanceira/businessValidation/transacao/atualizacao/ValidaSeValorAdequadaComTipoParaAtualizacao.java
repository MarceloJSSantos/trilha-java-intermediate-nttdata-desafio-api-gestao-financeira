package br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.atualizacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.cadastro.ValidaSeValorAdequadoComTipoParaCadastro;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidaSeValorAdequadaComTipoParaAtualizacao implements ValidacaoAtualizacaoTransacao{
    @Autowired
    private ValidaSeValorAdequadoComTipoParaCadastro validaSeValorAdequadoComTipoParaCadastro;

    @Override
    public void valida(TransacaoCadastroDTO dto) {
        var seTipoEValorNaoSaoNull = (dto.tipo() != null && dto.valor() != null);
        if (seTipoEValorNaoSaoNull) {
            validaSeValorAdequadoComTipoParaCadastro.valida(dto);
        }
    }
}

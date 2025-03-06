package br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.atualizacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.cadastro.ValidaSeMetodoAdequadoComTipoParaCadastro;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidaSeMetodoAdequadaComTipoParaAtualizacao implements ValidacaoAtualizacaoTransacao{
    @Autowired
    private ValidaSeMetodoAdequadoComTipoParaCadastro validaSeMetodoAdequadoComTipoParaCadastro;

    @Override
    public void valida(TransacaoCadastroDTO dto) {
        var seTipoEMetodoNaoSaoNull = (dto.tipo() != null && dto.metodo() != null);
        if (seTipoEMetodoNaoSaoNull) {
            validaSeMetodoAdequadoComTipoParaCadastro.valida(dto);
        }
    }
}

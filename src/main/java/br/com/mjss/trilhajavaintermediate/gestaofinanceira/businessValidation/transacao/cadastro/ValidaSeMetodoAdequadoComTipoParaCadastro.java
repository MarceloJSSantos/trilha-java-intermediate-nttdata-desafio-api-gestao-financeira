package br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.cadastro;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.exception.ValidacaoNegocioException;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;

public class ValidaSeMetodoAdequadoComTipoParaCadastro implements ValidacaoCadastroTransacao {
    @Override
    public void valida(TransacaoCadastroDTO dto) {
        var seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa = (dto.tipo() == TipoTransacao.DESPESA && !dto.metodo().getTipo().equals(TipoTransacao.DESPESA));
        var seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita = (dto.tipo() == TipoTransacao.RECEITA && !dto.metodo().getTipo().equals(TipoTransacao.RECEITA));
        var mensagem = "Metodo %s é inválido para tipo %s.".formatted(dto.metodo(), dto.tipo());

        if (seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa || seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita) {
            throw new ValidacaoNegocioException(mensagem);
        }
    }
}

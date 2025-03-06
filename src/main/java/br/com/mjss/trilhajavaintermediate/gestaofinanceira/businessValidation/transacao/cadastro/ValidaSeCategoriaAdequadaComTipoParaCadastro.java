package br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.cadastro;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.exception.ValidacaoNegocioException;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;

public class ValidaSeCategoriaAdequadaComTipoParaCadastro implements ValidacaoCadastroTransacao {
    @Override
    public void valida(TransacaoCadastroDTO dto) {
        var seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa = (dto.tipo() == TipoTransacao.DESPESA && !dto.categoria().getTipo().equals(TipoTransacao.DESPESA));
        var seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita = (dto.tipo() == TipoTransacao.RECEITA && !dto.categoria().getTipo().equals(TipoTransacao.RECEITA));
        var mensagem = "Categoria %s é inválida para tipo %s.".formatted(dto.categoria(), dto.tipo());

        if (seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa || seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita) {
            throw new ValidacaoNegocioException(mensagem);
        }
    }
}

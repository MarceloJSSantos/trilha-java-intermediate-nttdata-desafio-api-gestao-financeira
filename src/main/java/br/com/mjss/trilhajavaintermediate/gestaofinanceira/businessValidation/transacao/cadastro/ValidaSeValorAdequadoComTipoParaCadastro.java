package br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.cadastro;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.exception.ValidacaoNegocioException;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;

public class ValidaSeValorAdequadoComTipoParaCadastro implements ValidacaoCadastroTransacao {
    @Override
    public void valida(TransacaoCadastroDTO dto) {
        var seTipoTransacaoReceita = dto.tipo().equals(TipoTransacao.RECEITA);
        var seValorAtualPositivo = dto.valor().signum() == 1;
        var seValorAtualZero = dto.valor().signum() == 0;
        var seTipoTransacaoReceitaEValorAtualPositivo = (seTipoTransacaoReceita == seValorAtualPositivo);
        var mensagem = "O 'valor' %.2f não é apropriado para o 'tipo' %s.".formatted(dto.valor(), dto.tipo());

        if (!seTipoTransacaoReceitaEValorAtualPositivo || seValorAtualZero) {
            throw new ValidacaoNegocioException(mensagem);
        }
    }
}

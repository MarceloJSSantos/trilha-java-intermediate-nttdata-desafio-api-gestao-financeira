package br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.atualizacao;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.cadastro.ValidaSeCategoriaAdequadaComTipoParaCadastro;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidaSeCategoriaAdequadaComTipoParaAtualizacao implements ValidacaoAtualizacaoTransacao{
    @Autowired
    private ValidaSeCategoriaAdequadaComTipoParaCadastro validaSeCategoriaAdequadaComTipoParaCadastro;

    @Override
    public void valida(TransacaoCadastroDTO dto) {
        var seTipoECategoriaNaoSaoNull = (dto.tipo() != null && dto.categoria() != null);
        if (seTipoECategoriaNaoSaoNull) {
            validaSeCategoriaAdequadaComTipoParaCadastro.valida(dto);
        }
    }
}

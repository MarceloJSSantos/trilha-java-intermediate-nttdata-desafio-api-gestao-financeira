package br.com.mjss.trilhajavaintermediate.gestaofinanceira.service;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoComSaldoDadosListagemDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoDadosComSaldoDeUsuarioPorPeriodoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoComSaldoViewRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils.TratadorDeDatasUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SaldoTransacaoService {

    @Autowired
    private TransacaoComSaldoViewRepository repositoryTransacaoComSaldo;
    @Autowired
    private TratadorDeDatasUtils tratadorDatasUtils;

    public TransacaoDadosComSaldoDeUsuarioPorPeriodoDTO retornaTransacaoDadosComSaldoDeUsuarioPorPeriodoDTO(Pageable paginacao, Long usuarioId,
                                                                                                             String dataInicial, String dataFinal, Usuario usuario) {
        var dataAtualInicial = tratadorDatasUtils.converteParaLocalDateTime(dataInicial, false);
        var dataAtualFinal = tratadorDatasUtils.converteParaLocalDateTime(dataFinal, true);

        tratadorDatasUtils.validaSeDataInicialMenorIgualFinal(dataAtualInicial.toLocalDate(), dataAtualFinal.toLocalDate());
        var listaPaginada = repositoryTransacaoComSaldo
                .findAllByUsuarioAndDataHoraTransacaoBetween(paginacao, usuario, dataAtualInicial, dataAtualFinal)
                .map(TransacaoComSaldoDadosListagemDTO::new);
        var saldoAtual = retornaSaldo(usuario, dataAtualInicial, dataAtualFinal);

        var dataAnteriorInicial = tratadorDatasUtils.converteParaLocalDateTime(null, false);
        var dataAnteriorFinal = dataAtualInicial.minusSeconds(1L);
        var saldoAnterior = retornaSaldo(usuario, dataAnteriorInicial, dataAnteriorFinal);

        var resposta = new TransacaoDadosComSaldoDeUsuarioPorPeriodoDTO(dataAtualInicial
                .toLocalDate().format(tratadorDatasUtils.formatoDataDDMMAAAA), dataAtualFinal
                .toLocalDate().format(tratadorDatasUtils.formatoDataDDMMAAAA), saldoAnterior, saldoAtual, usuarioId,
                listaPaginada);
        return resposta;
    }

    private BigDecimal retornaSaldo(Usuario usuario, LocalDateTime dataIncial, LocalDateTime dataFinal) {
        var saldoTratado = BigDecimal.ZERO;
        var saldoAnterior = repositoryTransacaoComSaldo.findTopByUsuarioAndDataHoraTransacaoBetweenOrderByDataHoraTransacaoDescIdDesc(usuario, dataIncial, dataFinal);
        if (saldoAnterior.isPresent()) {
            saldoTratado = saldoAnterior.get().getSaldo();
        }

        return saldoTratado;
    }


}

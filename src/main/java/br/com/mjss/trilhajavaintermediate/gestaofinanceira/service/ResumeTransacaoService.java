package br.com.mjss.trilhajavaintermediate.gestaofinanceira.service;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo.ResumoTransacaoDeUsuarioPorPeriodoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo.ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo.ResumoTransacaoDeUsuarioPorPeriodoTipoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo.ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Transacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils.TratadorDeDatasUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ResumeTransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;
    @Autowired
    private TratadorDeDatasUtils tratadorDatasUtils;

    public ResumoTransacaoDeUsuarioPorPeriodoDTO retornaResumoTransacaoDeUsuarioPorPeriodoDTOComTipoECategoria(String dataInicial, String dataFinal, Usuario usuario) {
        var dataAtualInicial = tratadorDatasUtils.converteParaLocalDateTime(dataInicial, false);
        var dataAtualFinal = tratadorDatasUtils.converteParaLocalDateTime(dataFinal, true);

        tratadorDatasUtils.validaSeDataInicialMenorIgualFinal(dataAtualInicial.toLocalDate(), dataAtualFinal.toLocalDate());
        var transacoes = transacaoRepository.findAllByUsuarioAndDataHoraTransacaoBetween(usuario, dataAtualInicial, dataAtualFinal);

        Map<TipoTransacao, ResumoTransacaoDeUsuarioPorPeriodoTipoDTO> resumoMap = new HashMap<>();
        int quantidadeTransacoes = 0;
        BigDecimal totalValor = BigDecimal.ZERO;

        for (Transacao transacao : transacoes) {
            quantidadeTransacoes++;
            totalValor = totalValor.add(transacao.getValor());

            resumoMap.computeIfAbsent(transacao.getTipo(), tipo -> new ResumoTransacaoDeUsuarioPorPeriodoTipoDTO(tipo, 0, BigDecimal.ZERO));

            ResumoTransacaoDeUsuarioPorPeriodoTipoDTO tipoAtual = resumoMap.get(transacao.getTipo());
            tipoAtual.setQuantidadeTransacoes(tipoAtual.getQuantidadeTransacoes() + 1);
            tipoAtual.setTotalValor(tipoAtual.getTotalValor().add(transacao.getValor()));

            Optional<ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO> categoriaExistente = tipoAtual.getCategorias()
                    .stream().filter(c -> c.getCategoria() == transacao.getCategoria()).findFirst();

            if (categoriaExistente.isPresent()) {
                ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO categoriaDTO = categoriaExistente.get();
                categoriaDTO.setQuantidadeTransacoes(categoriaDTO.getQuantidadeTransacoes() + 1);
                categoriaDTO.setTotalValor(categoriaDTO.getTotalValor().add(transacao.getValor()));
            } else {
                ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO novaCategoriaDTO = new ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO(transacao.getCategoria(), 1, transacao.getValor());
                tipoAtual.adicionarCategoria(novaCategoriaDTO);
            }
        }

        List<ResumoTransacaoDeUsuarioPorPeriodoTipoDTO> tipos = new ArrayList<>(resumoMap.values());
        var resposta = new ResumoTransacaoDeUsuarioPorPeriodoDTO(
                dataAtualInicial.toLocalDate().format(tratadorDatasUtils.formatoDataDDMMAAAA),
                dataAtualFinal.toLocalDate().format(tratadorDatasUtils.formatoDataDDMMAAAA),
                usuario.getId(), quantidadeTransacoes, totalValor, tipos);
        return resposta;
    }

    public ResumoTransacaoDeUsuarioPorPeriodoDTO retornaResumoTransacaoDeUsuarioPorPeriodoDTOComTipoEMetodo(String dataInicial, String dataFinal, Usuario usuario) {
        var dataAtualInicial = tratadorDatasUtils.converteParaLocalDateTime(dataInicial, false);
        var dataAtualFinal = tratadorDatasUtils.converteParaLocalDateTime(dataFinal, true);

        tratadorDatasUtils.validaSeDataInicialMenorIgualFinal(dataAtualInicial.toLocalDate(), dataAtualFinal.toLocalDate());
        var transacoes = transacaoRepository.findAllByUsuarioAndDataHoraTransacaoBetween(usuario, dataAtualInicial, dataAtualFinal);

        Map<TipoTransacao, ResumoTransacaoDeUsuarioPorPeriodoTipoDTO> resumoMap = new HashMap<>();
        int quantidadeTransacoes = 0;
        BigDecimal totalValor = BigDecimal.ZERO;

        for (Transacao transacao : transacoes) {
            quantidadeTransacoes++;
            totalValor = totalValor.add(transacao.getValor());

            resumoMap.computeIfAbsent(transacao.getTipo(), tipo -> new ResumoTransacaoDeUsuarioPorPeriodoTipoDTO(tipo, 0, BigDecimal.ZERO));

            ResumoTransacaoDeUsuarioPorPeriodoTipoDTO tipoAtual = resumoMap.get(transacao.getTipo());
            tipoAtual.setQuantidadeTransacoes(tipoAtual.getQuantidadeTransacoes() + 1);
            tipoAtual.setTotalValor(tipoAtual.getTotalValor().add(transacao.getValor()));

            Optional<ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO> metodoExistente = tipoAtual.getMetodos()
                    .stream().filter(c -> c.getMetodo() == transacao.getMetodo()).findFirst();

            if (metodoExistente.isPresent()) {
                ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO metodoDTO = metodoExistente.get();
                metodoDTO.setQuantidadeTransacoes(metodoDTO.getQuantidadeTransacoes() + 1);
                metodoDTO.setTotalValor(metodoDTO.getTotalValor().add(transacao.getValor()));
            } else {
                ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO novoMetodoDTO = new ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO(transacao.getMetodo(), 1, transacao.getValor());
                tipoAtual.adicionarMetodo(novoMetodoDTO);
            }
        }

        List<ResumoTransacaoDeUsuarioPorPeriodoTipoDTO> tipos = new ArrayList<>(resumoMap.values());
        var resposta = new ResumoTransacaoDeUsuarioPorPeriodoDTO(
                dataAtualInicial.toLocalDate().format(tratadorDatasUtils.formatoDataDDMMAAAA),
                dataAtualFinal.toLocalDate().format(tratadorDatasUtils.formatoDataDDMMAAAA),
                usuario.getId(), quantidadeTransacoes, totalValor, tipos);
        return resposta;
    }
}

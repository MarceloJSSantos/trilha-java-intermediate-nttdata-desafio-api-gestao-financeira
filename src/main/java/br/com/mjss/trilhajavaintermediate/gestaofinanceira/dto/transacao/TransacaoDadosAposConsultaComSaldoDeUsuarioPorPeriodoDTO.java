package br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public record TransacaoDadosAposConsultaComSaldoDeUsuarioPorPeriodoDTO(
        String dataInicial,
        String dataFinal,
        BigDecimal saldoAnterior,
        BigDecimal saldoAtual,
        Long usuarioId,
        Page<TransacaoComSaldoDadosListagemDTO> transacoesPeriodo
) {}

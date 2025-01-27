package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao;

public enum MetodoSaida implements Metodo{
    CARTAO_CREDITO,
    CARTAO_DEBITO,
    PIX,
    OUTROS
}

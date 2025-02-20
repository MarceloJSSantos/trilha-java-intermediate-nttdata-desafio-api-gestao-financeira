package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao;

public enum Metodo {
    TRANSFERENCIA(TipoTransacao.RECEITA),
    OUTRO_METODO_DE_RECEITA(TipoTransacao.RECEITA),

    CARTAO_CREDITO(TipoTransacao.DESPESA),
    CARTAO_DEBITO(TipoTransacao.DESPESA),
    PIX(TipoTransacao.DESPESA),
    OUTRO_METODO_DE_DESPESA(TipoTransacao.DESPESA);

    private final TipoTransacao tipo;

    Metodo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }
}

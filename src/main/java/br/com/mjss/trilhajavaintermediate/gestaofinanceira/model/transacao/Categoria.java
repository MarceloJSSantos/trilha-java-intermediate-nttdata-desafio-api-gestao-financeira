package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao;

public enum Categoria {
    SALARIO(TipoTransacao.RECEITA),
    PROVENTOS(TipoTransacao.RECEITA),
    OUTRA_DE_RECEITA(TipoTransacao.RECEITA),

    ALIMENTACAO(TipoTransacao.DESPESA),
    PASSEIO(TipoTransacao.DESPESA),
    TRANSPORTE(TipoTransacao.DESPESA),
    EDUCACAO(TipoTransacao.DESPESA),
    OUTRA_DE_DESPESA(TipoTransacao.DESPESA);

    private final TipoTransacao tipo;

    Categoria(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

}

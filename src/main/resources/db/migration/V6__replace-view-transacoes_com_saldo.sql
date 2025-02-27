CREATE OR REPLACE VIEW view_transacoes_com_saldo AS
SELECT
    t.id,
    t.data_hora_transacao,
    t.tipo,
    t.categoria,
    t.descricao,
    t.valor,
    t.metodo,
    t.usuario_id,
    SUM(t.valor) OVER (PARTITION BY t.usuario_id ORDER BY t.data_hora_transacao, t.id) AS saldo
FROM
    transacoes t;
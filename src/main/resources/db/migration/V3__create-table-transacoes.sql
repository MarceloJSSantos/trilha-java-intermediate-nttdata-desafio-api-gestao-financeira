CREATE TABLE transacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_hora_transacao DATETIME,
    tipo VARCHAR(100),
    categoria VARCHAR(100),
    descricao VARCHAR(100),
    valor DECIMAL(19, 2),
    metodo VARCHAR(100),
    saldo DECIMAL(19, 2),
    usuario_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
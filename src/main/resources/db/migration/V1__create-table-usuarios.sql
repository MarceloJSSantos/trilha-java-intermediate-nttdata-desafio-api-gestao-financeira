CREATE TABLE usuarios (
  id BIGINT AUTO_INCREMENT NOT NULL,
   nome VARCHAR(120) NOT NULL,
   login VARCHAR(100) NOT NULL UNIQUE,
   senha VARCHAR(255) NOT NULL,
   CONSTRAINT pk_usuarios PRIMARY KEY (id)
);
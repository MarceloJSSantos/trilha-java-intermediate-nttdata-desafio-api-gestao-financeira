ALTER TABLE `usuarios`
ADD COLUMN `ativo` boolean NOT NULL DEFAULT 1;

UPDATE `usuarios`
SET `ativo` = 1;
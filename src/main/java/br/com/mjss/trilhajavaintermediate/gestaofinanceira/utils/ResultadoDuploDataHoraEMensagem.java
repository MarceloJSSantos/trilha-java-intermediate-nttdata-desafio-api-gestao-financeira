package br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class ResultadoDuploDataHoraEMensagem {
    private LocalDateTime dataHora;
    private List<String> motivos;
}

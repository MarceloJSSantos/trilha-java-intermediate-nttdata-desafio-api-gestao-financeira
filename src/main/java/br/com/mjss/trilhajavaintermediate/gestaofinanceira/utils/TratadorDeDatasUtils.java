package br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.exception.ValidacaoNegocioException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class TratadorDeDatasUtils {

    public  DateTimeFormatter formatoDataDDMMAAAA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LocalDateTime converteParaLocalDateTime(String data, boolean finalDia) {
        data = retornaDataTratada(data, finalDia);

        var horario = (finalDia) ? " 23:59:59" : " 00:00:00";

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(data + horario, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            return localDateTime;
        } catch (DateTimeParseException e) {
            throw new ValidacaoNegocioException("A data informada '%s' não está no formato válido 'dd/MM/yyyy'.".formatted(data));
        }
    }

    public String retornaDataTratada(String dataTratada, boolean finalDia) {
        if (dataTratada == null) {
            if (finalDia) {
                dataTratada = LocalDate.now().format(formatoDataDDMMAAAA);
            } else {
                dataTratada = "01/01/1900";
            }
        }
        return dataTratada;
    }

    public void validaSeDataInicialMenorIgualFinal(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial.isAfter(dataFinal)) {
            throw new ValidacaoNegocioException("A data inicial '%s' não pode ser maior que a data final '%s'."
                    .formatted(dataInicial.format(formatoDataDDMMAAAA),
                            dataFinal.format(formatoDataDDMMAAAA)));
        }
    }
}

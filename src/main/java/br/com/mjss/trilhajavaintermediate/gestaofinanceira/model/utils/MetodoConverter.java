package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.utils;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Metodo;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.MetodoEntrada;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.MetodoSaida;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MetodoConverter implements AttributeConverter<Metodo, String> {

    @Override
    public String convertToDatabaseColumn(Metodo metodo) {
        if (metodo == null) {
            return null;
        }
        return metodo.getClass().getSimpleName() + ":" + metodo;
    }

    @Override
    public Metodo convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] parts = dbData.split(":");
        String enumType = parts[0];
        String enumValue = parts[1];

        switch (enumType) {
            case "MetodoEntrada":
                return MetodoEntrada.valueOf(enumValue);
            case "MetodoSaida":
                return MetodoSaida.valueOf(enumValue);
            default:
                throw new IllegalArgumentException("Tipo de método desconhecido: " + enumType);
        }
    }
}

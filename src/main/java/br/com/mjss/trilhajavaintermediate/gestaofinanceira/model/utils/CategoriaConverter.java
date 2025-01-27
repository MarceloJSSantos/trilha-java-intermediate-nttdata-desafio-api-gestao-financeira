package br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.utils;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.CategoriaEntrada;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.CategoriaSaida;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CategoriaConverter implements AttributeConverter<Categoria, String> {

    @Override
    public String convertToDatabaseColumn(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return categoria.getClass().getSimpleName() + ":" + categoria;
    }

    @Override
    public Categoria convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] parts = dbData.split(":");
        String enumType = parts[0];
        String enumValue = parts[1];

        switch (enumType) {
            case "CategoriaEntrada":
                return CategoriaEntrada.valueOf(enumValue);
            case "CategoriaSaida":
                return CategoriaSaida.valueOf(enumValue);
            default:
                throw new IllegalArgumentException("Tipo de categoria desconhecido: " + enumType);
        }
    }
}

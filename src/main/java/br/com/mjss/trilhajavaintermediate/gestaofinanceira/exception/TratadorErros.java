package br.com.mjss.trilhajavaintermediate.gestaofinanceira.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorErros {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity trataErro400(MethodArgumentNotValidException e){
        var listaErros = e.getFieldErrors();
        var a = listaErros.stream().map(DadosErroValidacao::new).toList();

        ObjectNode errorJson = objectMapper.createObjectNode();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (DadosErroValidacao item : a) {
            arrayNode.add(objectMapper.valueToTree(item));
        }

        errorJson.put("erro", e.getClass().getSimpleName());
        errorJson.set("mensagem", arrayNode);
        return ResponseEntity.badRequest().body(errorJson);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity trataErro500(HttpMessageNotReadableException e){
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("erro", e.getClass().getSimpleName());
        errorJson.put("mensagem", e.getMessage());

        return ResponseEntity.internalServerError().body(errorJson);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity trataErro404(EntityNotFoundException e){
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("erro", e.getClass().getSimpleName());
        errorJson.put("mensagem", e.getMessage());

        return ResponseEntity.badRequest().body(errorJson);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity trataErro500(DataIntegrityViolationException e){
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("erro", e.getClass().getSimpleName());
        errorJson.put("mensagem", e.getMessage());

        return ResponseEntity.internalServerError().body(errorJson);
    }

    @ExceptionHandler(ValidacaoNegocioException.class)
    public ResponseEntity trataErro400(ValidacaoNegocioException e){
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("erro", e.getClass().getSimpleName());
        errorJson.put("mensagem", e.getMessage());

        return ResponseEntity.badRequest().body(errorJson);
    }

    private record DadosErroValidacao(String campo, String mensagem){
        public DadosErroValidacao(FieldError error){
            this(error.getField(), error.getDefaultMessage());
        }
    }
}

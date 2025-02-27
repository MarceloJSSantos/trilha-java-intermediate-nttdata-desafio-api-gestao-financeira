package br.com.mjss.trilhajavaintermediate.gestaofinanceira.service;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.TransacaoCadastroDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.importacaoPlanilha.TransacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.exception.ValidacaoNegocioException;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Metodo;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Transacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class ImportaTransacaoService {
    public ResultadoDuploListasTransacoesImportacao importaTransacoesDePlanilha(MultipartFile file, Usuario usuario){
        int quantidadeLinhas = 0;
        List<Transacao> listaTransacoesProcessdas = new ArrayList<>();
        List<TransacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO> listaTransacoesNaoProcessdas = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // pular cabeçalho

            while (rowIterator.hasNext()) {
                List<String> listaMotivos = new ArrayList<>();
                Row row = rowIterator.next();
                if (seLinhaVazia(row)) {
                    break;
                }

                var dataHoraPlanilha = row.getCell(0).getDateCellValue();
                var dataHoraTransacao = retornaDataHoraEMensagem(dataHoraPlanilha, listaMotivos).getDataHora();

                var  tipoTransacaoPlanilha = row.getCell(4).getStringCellValue();
                var  tipoTransacao = retornaTipoTransacaoEMensagem(tipoTransacaoPlanilha, listaMotivos).getTipoTransacao();

                var categoriaPlanilha = row.getCell(1).getStringCellValue();
                var categoria = retornaCategoriaEMensagem(categoriaPlanilha, listaMotivos).getCategoria();

                var descricao = row.getCell(2).getStringCellValue();
                var valor = new BigDecimal(row.getCell(3).getNumericCellValue());

                var metodoPlanilha = row.getCell(5).getStringCellValue();
                var metodo = retornaMetodoEMensagem(metodoPlanilha, listaMotivos).getMetodo();

                var dto = new TransacaoCadastroDTO(dataHoraTransacao, tipoTransacao, categoria, descricao, valor,
                        metodo, usuario.getId());

                if(dto.tipo() != null && dto.categoria() != null)
                    validaSeCategoriaAdequadaComTipoParaCadastroPlanilha(dto.tipo(), dto.categoria(), listaMotivos);

                if(dto.tipo() != null && dto.metodo() != null)
                    validaSeMetodoAdequadoComTipoParaCadastroPlanilha(dto.tipo(), dto.metodo(), listaMotivos);

                if(dto.tipo() != null && dto.valor() != null)
                    validaSeValorAdequadoComTipoParaCadastroPlanilha(dto.tipo(), dto.valor(), listaMotivos);

                var transacao = new Transacao(usuario, dto);
                if (listaMotivos.isEmpty()){
                    listaTransacoesProcessdas.add(transacao);
                } else{
                    var transacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO = new TransacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO(quantidadeLinhas+2, listaMotivos);
                    listaTransacoesNaoProcessdas.add(transacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO);
                }
                quantidadeLinhas ++;
            }
        } catch (IOException e) {
            throw new ValidacaoNegocioException(e.getMessage());
        }

        return new ResultadoDuploListasTransacoesImportacao(listaTransacoesProcessdas, listaTransacoesNaoProcessdas);
    }

    private ResultadoDuploTipoTransacaoEMensagem retornaTipoTransacaoEMensagem(String valor, List<String> motivos) {
        var resultadoTipo = validaEnumTipoTransacao(valor);
        TipoTransacao tipoTransacao = null;
        if (resultadoTipo == null){
            tipoTransacao = TipoTransacao.valueOf(valor);
        } else {
            motivos.add("[TIPO]: " + resultadoTipo);
        }

        return new ResultadoDuploTipoTransacaoEMensagem(tipoTransacao, motivos);
    }

    private ResultadoDuploDataHoraEMensagem retornaDataHoraEMensagem(Date data, List<String> motivos) {
        var resultadoDataHora = validaDataEHora(data);
        LocalDateTime dataHora = null;
        if (resultadoDataHora == null){
            Instant instant = data.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            dataHora = LocalDateTime.ofInstant(instant, zoneId);
        } else {
            motivos.add("[DATA/HORA]: " + resultadoDataHora);
        }

        return new ResultadoDuploDataHoraEMensagem(dataHora, motivos);
    }

    private ResultadoDuploCategoriaEMensagem retornaCategoriaEMensagem(String valor, List<String> motivos) {
        var resultaCategoria = validaEnumCategoria(valor);
        Categoria categoria = null;
        if (resultaCategoria == null){
            categoria = Categoria.valueOf(valor);
        } else {
            motivos.add("[CATEGORIA]: " + resultaCategoria);
        }

        return new ResultadoDuploCategoriaEMensagem(categoria, motivos);
    }

    private ResultadoDuploMetodoEMensagem retornaMetodoEMensagem(String valor, List<String> motivos) {
        var resultaMetodo = validaEnumMetodo(valor);
        Metodo metodo = null;
        if (resultaMetodo == null){
            metodo = Metodo.valueOf(valor);
        } else {
            motivos.add("[MÉTODO]: " + resultaMetodo);
        }

        return new ResultadoDuploMetodoEMensagem(metodo, motivos);
    }

    private String validaEnumTipoTransacao(String tipoStr) {
        String mensagem = null;
        if(tipoStr.isBlank()) {
            mensagem = "O 'tipo' não pode ser 'null' ou 'vazio'";
        } else if (!enumTipoTransacaoEValido(tipoStr)){
            var listaValoresEsperados =  Arrays.stream(TipoTransacao.values()).toList();
            mensagem = "O 'tipo' passado '%s' não é valido, é esperado '%s'.".formatted(tipoStr, listaValoresEsperados);
        }
        return mensagem;
    }

    private String validaDataEHora(Date data) {
        String mensagem = null;
        if(data == null) {
            mensagem = "A 'DataHora' não pode ser 'null' ou 'vazio'";
        } else if (!dataHoraEValida(data)){
            mensagem = "A 'DataHora' passada '%s' não é valida.".formatted(data);
        }
        return mensagem;
    }

    private String validaEnumCategoria(String tipoStr) {
        String mensagem = null;
        if(tipoStr.isBlank()) {
            mensagem = "A 'categoria' não pode ser 'null' ou 'vazio'";
        } else if (!enumCategoriaEValido(tipoStr)){
            var listaValoresEsperados =  Arrays.stream(Categoria.values()).toList();
            mensagem = "A 'categoria' passada '%s' não é valida, é esperado '%s'.".formatted(tipoStr, listaValoresEsperados);
        }
        return mensagem;
    }

    private String validaEnumMetodo(String tipoStr) {
        String mensagem = null;
        if(tipoStr.isBlank()) {
            mensagem = "O 'método' não pode ser 'null' ou 'vazio'";
        } else if (!enumMetodoEValido(tipoStr)){
            var listaValoresEsperados =  Arrays.stream(Metodo.values()).toList();
            mensagem = "O 'método' passado '%s' não é valido, é esperado '%s'.".formatted(tipoStr, listaValoresEsperados);
        }
        return mensagem;
    }

    private static boolean enumTipoTransacaoEValido(String vvalor) {
        for (TipoTransacao tipo : TipoTransacao.values()) {
            if (tipo.name().equals(vvalor)) {
                return true;
            }
        }
        return false;
    }

    private static boolean dataHoraEValida(Date valor) {
        try{
            Instant instant = valor.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            var dataHora = LocalDateTime.ofInstant(instant, zoneId);
            return true;
        } catch (RuntimeException e){
            return false;
        }
    }

    private static boolean enumCategoriaEValido(String valor) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.name().equals(valor)) {
                return true;
            }
        }
        return false;
    }

    private static boolean enumMetodoEValido(String valor) {
        for (Metodo metodo : Metodo.values()) {
            if (metodo.name().equals(valor)) {
                return true;
            }
        }
        return false;
    }

    private boolean seLinhaVazia(Row linha) {
        if (linha.getPhysicalNumberOfCells() == 0) {
            return true;
        }

        for (int i = 0; i < linha.getPhysicalNumberOfCells(); i++) {
            Cell celula = linha.getCell(i);
            if (celula != null && celula.getCellType() != CellType.BLANK) {
                return false;
            }
        }

        return true;
    }

    private void validaSeCategoriaAdequadaComTipoParaCadastroPlanilha(TipoTransacao tipo, Categoria categoria, List<String> motivos) {
        var seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa = (tipo == TipoTransacao.DESPESA && !categoria.getTipo().equals(TipoTransacao.DESPESA));
        var seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita = (tipo == TipoTransacao.RECEITA && !categoria.getTipo().equals(TipoTransacao.RECEITA));
        var mensagem = "[CATEGORIA/TIPO]: Categoria %s é inválida para tipo %s.".formatted(categoria, tipo);

        if (seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa || seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita) {
            motivos.add(mensagem);
        }
    }

    private void validaSeMetodoAdequadoComTipoParaCadastroPlanilha(TipoTransacao tipo, Metodo metodo, List<String> motivos) {
        var seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa = (tipo == TipoTransacao.DESPESA && !metodo.getTipo().equals(TipoTransacao.DESPESA));
        var seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita = (tipo == TipoTransacao.RECEITA && !metodo.getTipo().equals(TipoTransacao.RECEITA));
        var mensagem = "[MÉTODO/TIPO]: Metodo %s é inválido para tipo %s.".formatted(metodo, tipo);

        if (seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa || seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita) {
            motivos.add(mensagem);
        }
    }

    private void validaSeValorAdequadoComTipoParaCadastroPlanilha(TipoTransacao tipo, BigDecimal valorAtual, List<String> motivos) {
        var seTipoTransacaoReceita = tipo.equals(TipoTransacao.RECEITA);
        var seValorAtualPositivo = valorAtual.signum() == 1;
        var seValorAtualZero = valorAtual.signum() == 0;
        var seTipoTransacaoReceitaEValorAtualPositivo = (seTipoTransacaoReceita == seValorAtualPositivo);
        var mensagem = "[VALOR/TIPO]: O 'valor' %.2f não é apropriado para o 'tipo' %s.".formatted(valorAtual, tipo);

        if (!seTipoTransacaoReceitaEValorAtualPositivo || seValorAtualZero) {
            motivos.add(mensagem);
        }
    }
}

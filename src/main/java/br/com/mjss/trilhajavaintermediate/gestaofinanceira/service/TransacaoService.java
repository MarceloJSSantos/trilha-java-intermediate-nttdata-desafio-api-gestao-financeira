package br.com.mjss.trilhajavaintermediate.gestaofinanceira.service;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.*;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.importacaoPlanilha.TransacaoAposCadastroPlanilhaPrincipalDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.importacaoPlanilha.TransacaoAposCadastroPlanilhaTransacaoNaoProcessadaDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo.ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo.ResumoTransacaoDeUsuarioPorPeriodoTipoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo.ResumoTransacaoDeUsuarioPorPeriodoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo.ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.exception.ValidacaoNegocioException;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Metodo;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Transacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoComSaldoViewRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.UsuarioRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils.ResultadoDuploCategoriaEMensagem;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils.ResultadoDuploDataHoraEMensagem;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils.ResultadoDuploMetodoEMensagem;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.utils.ResultadoDuploTipoTransacaoEMensagem;
import jakarta.persistence.EntityNotFoundException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class TransacaoService {

    DateTimeFormatter formatoDataDDMMAAAA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @Autowired
    private TransacaoRepository repository;
    @Autowired
    private TransacaoComSaldoViewRepository repositoryTransacaoComSaldo;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Transacao cadastrar(TransacaoCadastroDTO dto) {
        var usuario = usuarioRepository.getReferenceById(dto.idUsuario());
        var transacao = new Transacao(usuario, dto);

        validaSeUsuarioExiste(dto.idUsuario());

        validaSeCategoriaAdequadaComTipoParaCadastro(dto.tipo(), dto.categoria());
        validaSeMetodoAdequadoComTipoParaCadastro(dto.tipo(), dto.metodo());
        validaSeValorAdequadoComTipoParaCadastro(dto.tipo(), dto.valor());

        return repository.save(transacao);
    }

    public TransacaoDadosAposCadastroOuAtualizacaoDTO consultarTransacao(Long id) {
        validaSeTransacaoExiste(id);

        var transacao = repository.getReferenceById(id);
        var dto = new TransacaoDadosAposCadastroOuAtualizacaoDTO(transacao);
        return dto;
    }

    public void excluirTransacao(Long id) {
        validaSeTransacaoExiste(id);

        repository.deleteById(id);
    }

    public TransacaoDadosComSaldoDeUsuarioPorPeriodoDTO listarTransacoesComSaldoDeUsuarioPorPeriodo(Pageable paginacao, Long usuarioId, String dataInicial, String dataFinal) {
        validaSeUsuarioExiste(usuarioId);

        var usuario = usuarioRepository.getReferenceById(usuarioId);
        return retornaTransacaoDadosComSaldoDeUsuarioPorPeriodoDTO(paginacao, usuarioId, dataInicial, dataFinal, usuario);
    }

    public Transacao atualizarTransacao(TransacaoAtualizacaoDTO dto) {
        validaSeTransacaoExiste(dto.id());
        var transacao = repository.getReferenceById(dto.id());

        validaSeTipoCategoriaMetodoValorSaoEnviados(dto);

        validaSeCategoriaAdequadaComTipoParaAtualizacao(dto.tipo(), dto.categoria());
        validaSeMetodoAdequadaComTipoParaAtualizacao(dto.tipo(), dto.metodo());
        validaSeValorAdequadaComTipoParaAtualizacao(dto.tipo(), dto.valor());

        transacao.atualizarTransacao(dto);

        return transacao;
    }

    public ResumoTransacaoDeUsuarioPorPeriodoDTO ResumoDeUsuarioPorPeriodoETipoECategoria(Long usuarioId, String dataInicial, String dataFinal) {
        validaSeUsuarioExiste(usuarioId);

        var usuario = usuarioRepository.getReferenceById(usuarioId);
        return retornaResumoTransacaoDeUsuarioPorPeriodoDTOComTipoECategoria(dataInicial, dataFinal, usuario);
    }

    public ResumoTransacaoDeUsuarioPorPeriodoDTO ResumoDeUsuarioPorPeriodoETipoEMetodo(Long usuarioId, String dataInicial, String dataFinal) {
        validaSeUsuarioExiste(usuarioId);

        var usuario = usuarioRepository.getReferenceById(usuarioId);
        return retornaResumoTransacaoDeUsuarioPorPeriodoDTOComTipoEMetodo(dataInicial, dataFinal, usuario);
    }

    public TransacaoAposCadastroPlanilhaPrincipalDTO cadastrarTransacaoDePlanilhaExcel(MultipartFile file, Long usuarioId) {
        validaSeUsuarioExiste(usuarioId);
        var usuario = usuarioRepository.getReferenceById(usuarioId);

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
                        metodo, usuarioId);

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
            repository.saveAll(listaTransacoesProcessdas);
        } catch (IOException e) {
            throw new ValidacaoNegocioException(e.getMessage());
        }

        var quantidadeTransacoesProcessadas = listaTransacoesProcessdas.stream().count();
        var quantidadeTransacoesNaoProcessadas = listaTransacoesNaoProcessdas.stream().count();

        return new TransacaoAposCadastroPlanilhaPrincipalDTO(quantidadeLinhas, quantidadeTransacoesProcessadas, quantidadeTransacoesNaoProcessadas, listaTransacoesNaoProcessdas);
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
        } else if (!isValidEnumTipoTransacao(tipoStr)){
            var listaValoresEsperados =  Arrays.stream(TipoTransacao.values()).toList();
            mensagem = "O 'tipo' passado '%s' não é valido, é esperado '%s'.".formatted(tipoStr, listaValoresEsperados);
        }
        return mensagem;
    }

    private String validaDataEHora(Date data) {
        String mensagem = null;
        if(data == null) {
            mensagem = "A 'DataHora' não pode ser 'null' ou 'vazio'";
        } else if (!isValidDataHora(data)){
            mensagem = "A 'DataHora' passada '%s' não é valida.".formatted(data);
        }
        return mensagem;
    }

    private String validaEnumCategoria(String tipoStr) {
        String mensagem = null;
        if(tipoStr.isBlank()) {
            mensagem = "A 'categoria' não pode ser 'null' ou 'vazio'";
        } else if (!isValidEnumCategoria(tipoStr)){
            var listaValoresEsperados =  Arrays.stream(Categoria.values()).toList();
            mensagem = "A 'categoria' passada '%s' não é valida, é esperado '%s'.".formatted(tipoStr, listaValoresEsperados);
        }
        return mensagem;
    }

    private String validaEnumMetodo(String tipoStr) {
        String mensagem = null;
        if(tipoStr.isBlank()) {
            mensagem = "O 'método' não pode ser 'null' ou 'vazio'";
        } else if (!isValidEnumMetodo(tipoStr)){
            var listaValoresEsperados =  Arrays.stream(Metodo.values()).toList();
            mensagem = "O 'método' passado '%s' não é valido, é esperado '%s'.".formatted(tipoStr, listaValoresEsperados);
        }
        return mensagem;
    }

    private static boolean isValidEnumTipoTransacao(String value) {
        for (TipoTransacao tipo : TipoTransacao.values()) {
            if (tipo.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidDataHora(Date value) {
        try{
            Instant instant = value.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            var dataHora = LocalDateTime.ofInstant(instant, zoneId);
            return true;
        } catch (RuntimeException e){
            return false;
        }
    }

    private static boolean isValidEnumCategoria(String value) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidEnumMetodo(String value) {
        for (Metodo metodo : Metodo.values()) {
            if (metodo.name().equals(value)) {
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

    private void validaSeValorAdequadaComTipoParaAtualizacao(TipoTransacao tipo, BigDecimal valor) {
        var seTipoEValorNaoSaoNull = (tipo != null && valor != null);
        if (seTipoEValorNaoSaoNull) {
            validaSeValorAdequadoComTipoParaCadastro(tipo, valor);
        }
    }

    private void validaSeMetodoAdequadaComTipoParaAtualizacao(TipoTransacao tipo, Metodo metodo) {
        var seTipoEMetodoNaoSaoNull = (tipo != null && metodo != null);
        if (seTipoEMetodoNaoSaoNull) {
            validaSeMetodoAdequadoComTipoParaCadastro(tipo, metodo);
        }
    }

    private void validaSeCategoriaAdequadaComTipoParaAtualizacao(TipoTransacao tipo, Categoria categoria) {
        var seTipoECategoriaNaoSaoNull = (tipo != null && categoria != null);
        if (seTipoECategoriaNaoSaoNull) {
            validaSeCategoriaAdequadaComTipoParaCadastro(tipo, categoria);
        }
    }

    private void validaSeTipoCategoriaMetodoValorSaoEnviados(TransacaoAtualizacaoDTO dto) {
        var seTipoOuCategoriaENull = (dto.tipo() == null || dto.categoria() == null || dto.metodo() == null || dto.valor() == null);
        var seTipoECategoriaNaoENull = !(dto.tipo() == null && dto.categoria() == null && dto.metodo() == null && dto.valor() == null);
        if (seTipoECategoriaNaoENull) {
            if (seTipoOuCategoriaENull) {
                throw new ValidacaoNegocioException("Para atualizar 'tipo' ou 'categoria' ou 'metodo' ou 'valor' todos tem que ser enviados!");
            }
        }
    }

    private void validaSeDataInicialMenorIgualFinal(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial.isAfter(dataFinal)) {
            throw new ValidacaoNegocioException("A data inicial '%s' não pode ser maior que a data final '%s'.".formatted(dataInicial.format(formatoDataDDMMAAAA), dataFinal.format(formatoDataDDMMAAAA)));
        }
    }

    private void validaSeUsuarioExiste(Long usuarioId) {
        var seExisteUsuario = (usuarioRepository.existsById(usuarioId));

        if (!seExisteUsuario) {
            throw new EntityNotFoundException("O usuário com ID '%s' não foi encontrado!".formatted(usuarioId));
        }
    }

    private void validaSeTransacaoExiste(Long id) {
        var seExisteTransacao = (repository.existsById(id));

        if (!seExisteTransacao) {
            throw new EntityNotFoundException("A transação com ID '%s' não foi encontrada!".formatted(id));
        }
    }

    private void validaSeCategoriaAdequadaComTipoParaCadastro(TipoTransacao tipo, Categoria categoria) {
        var seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa = (tipo == TipoTransacao.DESPESA && !categoria.getTipo().equals(TipoTransacao.DESPESA));
        var seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita = (tipo == TipoTransacao.RECEITA && !categoria.getTipo().equals(TipoTransacao.RECEITA));
        var mensagem = "Categoria %s é inválida para tipo %s.".formatted(categoria, tipo);

        if (seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa || seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita) {
            throw new ValidacaoNegocioException(mensagem);
        }
    }

    private void validaSeMetodoAdequadoComTipoParaCadastro(TipoTransacao tipo, Metodo metodo) {
        var seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa = (tipo == TipoTransacao.DESPESA && !metodo.getTipo().equals(TipoTransacao.DESPESA));
        var seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita = (tipo == TipoTransacao.RECEITA && !metodo.getTipo().equals(TipoTransacao.RECEITA));
        var mensagem = "Metodo %s é inválido para tipo %s.".formatted(metodo, tipo);

        if (seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa || seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita) {
            throw new ValidacaoNegocioException(mensagem);
        }
    }

    private void validaSeValorAdequadoComTipoParaCadastro(TipoTransacao tipo, BigDecimal valorAtual) {
        var seTipoTransacaoReceita = tipo.equals(TipoTransacao.RECEITA);
        var seValorAtualPositivo = valorAtual.signum() == 1;
        var seValorAtualZero = valorAtual.signum() == 0;
        var seTipoTransacaoReceitaEValorAtualPositivo = (seTipoTransacaoReceita == seValorAtualPositivo);
        var mensagem = "O 'valor' %.2f não é apropriado para o 'tipo' %s.".formatted(valorAtual, tipo);

        if (!seTipoTransacaoReceitaEValorAtualPositivo || seValorAtualZero) {
            throw new ValidacaoNegocioException(mensagem);
        }
    }

    private LocalDateTime converteParaLocalDateTime(String data, boolean finalDia) {
        data = retornaDataTratada(data, finalDia);

        var horario = (finalDia) ? " 23:59:59" : " 00:00:00";

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(data + horario, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            return localDateTime;
        } catch (DateTimeParseException e) {
            throw new ValidacaoNegocioException("A data informada '%s' não está no formato válido 'dd/MM/yyyy'.".formatted(data));
        }
    }

    private String retornaDataTratada(String dataTratada, boolean finalDia) {
        if (dataTratada == null) {
            if (finalDia) {
                dataTratada = LocalDate.now().format(formatoDataDDMMAAAA);
            } else {
                dataTratada = "01/01/1900";
            }
        }
        return dataTratada;
    }

    private BigDecimal retornaSaldo(Usuario usuario, LocalDateTime dataIncial, LocalDateTime dataFinal) {
        var saldoTratado = BigDecimal.ZERO;
        var saldoAnterior = repositoryTransacaoComSaldo.findTopByUsuarioAndDataHoraTransacaoBetweenOrderByDataHoraTransacaoDescIdDesc(usuario, dataIncial, dataFinal);
        if (saldoAnterior.isPresent()) {
            saldoTratado = saldoAnterior.get().getSaldo();
        }

        return saldoTratado;
    }

    private TransacaoDadosComSaldoDeUsuarioPorPeriodoDTO retornaTransacaoDadosComSaldoDeUsuarioPorPeriodoDTO(Pageable paginacao, Long usuarioId, String dataInicial, String dataFinal, Usuario usuario) {
        var dataAtualInicial = converteParaLocalDateTime(dataInicial, false);
        var dataAtualFinal = converteParaLocalDateTime(dataFinal, true);

        validaSeDataInicialMenorIgualFinal(dataAtualInicial.toLocalDate(), dataAtualFinal.toLocalDate());
        var listaPaginada = repositoryTransacaoComSaldo.findAllByUsuarioAndDataHoraTransacaoBetween(paginacao, usuario, dataAtualInicial, dataAtualFinal).map(TransacaoComSaldoDadosListagemDTO::new);
        var saldoAtual = retornaSaldo(usuario, dataAtualInicial, dataAtualFinal);

        var dataAnteriorInicial = converteParaLocalDateTime(null, false);
        var dataAnteriorFinal = dataAtualInicial.minusSeconds(1L);
        var saldoAnterior = retornaSaldo(usuario, dataAnteriorInicial, dataAnteriorFinal);

        var resposta = new TransacaoDadosComSaldoDeUsuarioPorPeriodoDTO(dataAtualInicial.toLocalDate().format(formatoDataDDMMAAAA), dataAtualFinal.toLocalDate().format(formatoDataDDMMAAAA), saldoAnterior, saldoAtual, usuarioId, listaPaginada);
        return resposta;
    }

    private ResumoTransacaoDeUsuarioPorPeriodoDTO retornaResumoTransacaoDeUsuarioPorPeriodoDTOComTipoECategoria(String dataInicial, String dataFinal, Usuario usuario) {
        var dataAtualInicial = converteParaLocalDateTime(dataInicial, false);
        var dataAtualFinal = converteParaLocalDateTime(dataFinal, true);

        validaSeDataInicialMenorIgualFinal(dataAtualInicial.toLocalDate(), dataAtualFinal.toLocalDate());
        var transacoes = repository.findAllByUsuarioAndDataHoraTransacaoBetween(usuario, dataAtualInicial, dataAtualFinal);

        Map<TipoTransacao, ResumoTransacaoDeUsuarioPorPeriodoTipoDTO> resumoMap = new HashMap<>();
        int quantidadeTransacoes = 0;
        BigDecimal totalValor = BigDecimal.ZERO;

        for (Transacao transacao : transacoes) {
            quantidadeTransacoes++;
            totalValor = totalValor.add(transacao.getValor());

            resumoMap.computeIfAbsent(transacao.getTipo(), tipo -> new ResumoTransacaoDeUsuarioPorPeriodoTipoDTO(tipo, 0, BigDecimal.ZERO));

            ResumoTransacaoDeUsuarioPorPeriodoTipoDTO tipoAtual = resumoMap.get(transacao.getTipo());
            tipoAtual.setQuantidadeTransacoes(tipoAtual.getQuantidadeTransacoes() + 1);
            tipoAtual.setTotalValor(tipoAtual.getTotalValor().add(transacao.getValor()));

            Optional<ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO> categoriaExistente = tipoAtual.getCategorias()
                    .stream().filter(c -> c.getCategoria() == transacao.getCategoria()).findFirst();

            if (categoriaExistente.isPresent()) {
                ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO categoriaDTO = categoriaExistente.get();
                categoriaDTO.setQuantidadeTransacoes(categoriaDTO.getQuantidadeTransacoes() + 1);
                categoriaDTO.setTotalValor(categoriaDTO.getTotalValor().add(transacao.getValor()));
            } else {
                ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO novaCategoriaDTO = new ResumoTransacaoDeUsuarioPorPeriodoTipoCategoriaDTO(transacao.getCategoria(), 1, transacao.getValor());
                tipoAtual.adicionarCategoria(novaCategoriaDTO);
            }
        }

        List<ResumoTransacaoDeUsuarioPorPeriodoTipoDTO> tipos = new ArrayList<>(resumoMap.values());
        var resposta = new ResumoTransacaoDeUsuarioPorPeriodoDTO(dataAtualInicial.toLocalDate().format(formatoDataDDMMAAAA),
                dataAtualFinal.toLocalDate().format(formatoDataDDMMAAAA), usuario.getId(), quantidadeTransacoes,
                totalValor, tipos);
        return resposta;
    }

    private ResumoTransacaoDeUsuarioPorPeriodoDTO retornaResumoTransacaoDeUsuarioPorPeriodoDTOComTipoEMetodo(String dataInicial, String dataFinal, Usuario usuario) {
        var dataAtualInicial = converteParaLocalDateTime(dataInicial, false);
        var dataAtualFinal = converteParaLocalDateTime(dataFinal, true);

        validaSeDataInicialMenorIgualFinal(dataAtualInicial.toLocalDate(), dataAtualFinal.toLocalDate());
        var transacoes = repository.findAllByUsuarioAndDataHoraTransacaoBetween(usuario, dataAtualInicial, dataAtualFinal);

        Map<TipoTransacao, ResumoTransacaoDeUsuarioPorPeriodoTipoDTO> resumoMap = new HashMap<>();
        int quantidadeTransacoes = 0;
        BigDecimal totalValor = BigDecimal.ZERO;

        for (Transacao transacao : transacoes) {
            quantidadeTransacoes++;
            totalValor = totalValor.add(transacao.getValor());

            resumoMap.computeIfAbsent(transacao.getTipo(), tipo -> new ResumoTransacaoDeUsuarioPorPeriodoTipoDTO(tipo, 0, BigDecimal.ZERO));

            ResumoTransacaoDeUsuarioPorPeriodoTipoDTO tipoAtual = resumoMap.get(transacao.getTipo());
            tipoAtual.setQuantidadeTransacoes(tipoAtual.getQuantidadeTransacoes() + 1);
            tipoAtual.setTotalValor(tipoAtual.getTotalValor().add(transacao.getValor()));

            Optional<ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO> metodoExistente = tipoAtual.getMetodos()
                    .stream().filter(c -> c.getMetodo() == transacao.getMetodo()).findFirst();

            if (metodoExistente.isPresent()) {
                ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO metodoDTO = metodoExistente.get();
                metodoDTO.setQuantidadeTransacoes(metodoDTO.getQuantidadeTransacoes() + 1);
                metodoDTO.setTotalValor(metodoDTO.getTotalValor().add(transacao.getValor()));
            } else {
                ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO novoMetodoDTO = new ResumoTransacaoDeUsuarioPorPeriodoTipoMetodoDTO(transacao.getMetodo(), 1, transacao.getValor());
                tipoAtual.adicionarMetodo(novoMetodoDTO);
            }
        }

        List<ResumoTransacaoDeUsuarioPorPeriodoTipoDTO> tipos = new ArrayList<>(resumoMap.values());
        var resposta = new ResumoTransacaoDeUsuarioPorPeriodoDTO(dataAtualInicial.toLocalDate().format(formatoDataDDMMAAAA),
                dataAtualFinal.toLocalDate().format(formatoDataDDMMAAAA), usuario.getId(), quantidadeTransacoes,
                totalValor, tipos);
        return resposta;
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

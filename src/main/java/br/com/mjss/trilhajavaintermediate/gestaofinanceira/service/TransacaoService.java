package br.com.mjss.trilhajavaintermediate.gestaofinanceira.service;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.atualizacao.ValidacaoAtualizacaoTransacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.businessValidation.transacao.cadastro.ValidacaoCadastroTransacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.*;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.importacaoPlanilha.TransacaoAposCadastroPlanilhaPrincipalDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo.ResumoTransacaoDeUsuarioPorPeriodoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.exception.ValidacaoNegocioException;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Metodo;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Transacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository repository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ImportaTransacaoService importaTransacaoService;
    @Autowired
    private ResumeTransacaoService resumeTransacaoService;
    @Autowired
    private SaldoTransacaoService saldoTransacaoService;
    @Autowired
    private List<ValidacaoCadastroTransacao> validacaoCadastroTransacao;
    @Autowired
    private List<ValidacaoAtualizacaoTransacao> validacaoAtualizacaoTransacaos;

    public Transacao cadastrar(TransacaoCadastroDTO dto) {
        var usuario = usuarioRepository.getReferenceById(dto.idUsuario());
        var transacao = new Transacao(usuario, dto);

        validaSeUsuarioExiste(dto.idUsuario());

        validacaoCadastroTransacao.forEach(v -> v.valida(dto));

//        validaSeCategoriaAdequadaComTipoParaCadastro(dto.tipo(), dto.categoria());
//        validaSeMetodoAdequadoComTipoParaCadastro(dto.tipo(), dto.metodo());
//        validaSeValorAdequadoComTipoParaCadastro(dto.tipo(), dto.valor());

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
        return saldoTransacaoService.retornaTransacaoDadosComSaldoDeUsuarioPorPeriodoDTO(paginacao, usuarioId, dataInicial, dataFinal, usuario);
    }

    public Transacao atualizarTransacao(TransacaoAtualizacaoDTO dto) {
        validaSeTransacaoExiste(dto.id());
        var transacao = repository.getReferenceById(dto.id());

        validaSeTipoCategoriaMetodoValorSaoEnviados(dto);

        validacaoAtualizacaoTransacaos.forEach(v -> v.valida(dto));

        validaSeCategoriaAdequadaComTipoParaAtualizacao(dto.tipo(), dto.categoria());
        validaSeMetodoAdequadaComTipoParaAtualizacao(dto.tipo(), dto.metodo());
        validaSeValorAdequadaComTipoParaAtualizacao(dto.tipo(), dto.valor());

        transacao.atualizarTransacao(dto);

        return transacao;
    }

    public ResumoTransacaoDeUsuarioPorPeriodoDTO ResumoDeUsuarioPorPeriodoETipoECategoria(Long usuarioId, String dataInicial, String dataFinal) {
        validaSeUsuarioExiste(usuarioId);

        var usuario = usuarioRepository.getReferenceById(usuarioId);
        return resumeTransacaoService.retornaResumoTransacaoDeUsuarioPorPeriodoDTOComTipoECategoria(dataInicial, dataFinal, usuario);
    }

    public ResumoTransacaoDeUsuarioPorPeriodoDTO ResumoDeUsuarioPorPeriodoETipoEMetodo(Long usuarioId, String dataInicial, String dataFinal) {
        validaSeUsuarioExiste(usuarioId);

        var usuario = usuarioRepository.getReferenceById(usuarioId);
        return resumeTransacaoService.retornaResumoTransacaoDeUsuarioPorPeriodoDTOComTipoEMetodo(dataInicial, dataFinal, usuario);
    }

    public TransacaoAposCadastroPlanilhaPrincipalDTO cadastrarTransacaoDePlanilhaExcel(MultipartFile file, Long usuarioId) {
        validaSeUsuarioExiste(usuarioId);
        var usuario = usuarioRepository.getReferenceById(usuarioId);

        var listasTransacoes = importaTransacaoService.importaTransacoesDePlanilha(file, usuario);
        var quantidadeTransacoesProcessadas = listasTransacoes.getTransacoesProcessadas().stream().count();
        var quantidadeTransacoesNaoProcessadas = listasTransacoes.getTransacoesNaoProcessadas().stream().count();
        var quantidadeLinhas = quantidadeTransacoesProcessadas + quantidadeTransacoesNaoProcessadas;
        var listaTransacoesNaoProcessdas = listasTransacoes.getTransacoesNaoProcessadas();
        repository.saveAll(listasTransacoes.getTransacoesProcessadas());

        return new TransacaoAposCadastroPlanilhaPrincipalDTO(quantidadeLinhas, quantidadeTransacoesProcessadas,
                quantidadeTransacoesNaoProcessadas, listaTransacoesNaoProcessdas);
    }

//    private void validaSeValorAdequadaComTipoParaAtualizacao(TipoTransacao tipo, BigDecimal valor) {
//        var seTipoEValorNaoSaoNull = (tipo != null && valor != null);
//        if (seTipoEValorNaoSaoNull) {
//            validaSeValorAdequadoComTipoParaCadastro(tipo, valor);
//        }
//    }

//    private void validaSeMetodoAdequadaComTipoParaAtualizacao(TipoTransacao tipo, Metodo metodo) {
//        var seTipoEMetodoNaoSaoNull = (tipo != null && metodo != null);
//        if (seTipoEMetodoNaoSaoNull) {
//            validaSeMetodoAdequadoComTipoParaCadastro(tipo, metodo);
//        }
//    }

//    private void validaSeCategoriaAdequadaComTipoParaAtualizacao(TipoTransacao tipo, Categoria categoria) {
//        var seTipoECategoriaNaoSaoNull = (tipo != null && categoria != null);
//        if (seTipoECategoriaNaoSaoNull) {
//            validaSeCategoriaAdequadaComTipoParaCadastro(tipo, categoria);
//        }
//    }

    private void validaSeTipoCategoriaMetodoValorSaoEnviados(TransacaoAtualizacaoDTO dto) {
        var seTipoOuCategoriaENull = (dto.tipo() == null || dto.categoria() == null || dto.metodo() == null || dto.valor() == null);
        var seTipoECategoriaNaoENull = !(dto.tipo() == null && dto.categoria() == null && dto.metodo() == null && dto.valor() == null);
        if (seTipoECategoriaNaoENull) {
            if (seTipoOuCategoriaENull) {
                throw new ValidacaoNegocioException("Para atualizar 'tipo' ou 'categoria' ou 'metodo' ou 'valor' todos tem que ser enviados!");
            }
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

//    private void validaSeCategoriaAdequadaComTipoParaCadastro(TipoTransacao tipo, Categoria categoria) {
//        var seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa = (tipo == TipoTransacao.DESPESA && !categoria.getTipo().equals(TipoTransacao.DESPESA));
//        var seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita = (tipo == TipoTransacao.RECEITA && !categoria.getTipo().equals(TipoTransacao.RECEITA));
//        var mensagem = "Categoria %s é inválida para tipo %s.".formatted(categoria, tipo);
//
//        if (seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa || seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita) {
//            throw new ValidacaoNegocioException(mensagem);
//        }
//    }

//    private void validaSeMetodoAdequadoComTipoParaCadastro(TipoTransacao tipo, Metodo metodo) {
//        var seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa = (tipo == TipoTransacao.DESPESA && !metodo.getTipo().equals(TipoTransacao.DESPESA));
//        var seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita = (tipo == TipoTransacao.RECEITA && !metodo.getTipo().equals(TipoTransacao.RECEITA));
//        var mensagem = "Metodo %s é inválido para tipo %s.".formatted(metodo, tipo);
//
//        if (seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa || seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita) {
//            throw new ValidacaoNegocioException(mensagem);
//        }
//    }

//    private void validaSeValorAdequadoComTipoParaCadastro(TipoTransacao tipo, BigDecimal valorAtual) {
//        var seTipoTransacaoReceita = tipo.equals(TipoTransacao.RECEITA);
//        var seValorAtualPositivo = valorAtual.signum() == 1;
//        var seValorAtualZero = valorAtual.signum() == 0;
//        var seTipoTransacaoReceitaEValorAtualPositivo = (seTipoTransacaoReceita == seValorAtualPositivo);
//        var mensagem = "O 'valor' %.2f não é apropriado para o 'tipo' %s.".formatted(valorAtual, tipo);
//
//        if (!seTipoTransacaoReceitaEValorAtualPositivo || seValorAtualZero) {
//            throw new ValidacaoNegocioException(mensagem);
//        }
//    }
}

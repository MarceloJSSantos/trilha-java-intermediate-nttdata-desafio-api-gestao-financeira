package br.com.mjss.trilhajavaintermediate.gestaofinanceira.service;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.*;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Categoria;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Metodo;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.TipoTransacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.Transacao;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoComSaldoViewRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    public TransacaoDadosAposConsultaComSaldoDeUsuarioPorPeriodoDTO listarTransacoesComSaldoDeUsuarioPorPeriodo(Pageable paginacao, Long usuarioId, String dataInicial, String dataFinal) {
        validaSeUsuarioExiste(usuarioId);

        var usuario = usuarioRepository.getReferenceById(usuarioId);
        return getTransacaoDadosAposConsultaComSaldoDeUsuarioPorPeriodoDTO(paginacao, usuarioId, dataInicial, dataFinal, usuario);
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

    private static void validaSeTipoCategoriaMetodoValorSaoEnviados(TransacaoAtualizacaoDTO dto) {
        var seTipoOuCategoriaENull = (dto.tipo() == null || dto.categoria() == null || dto.metodo() == null || dto.valor() == null);
        var seTipoECategoriaNaoENull = !(dto.tipo() == null && dto.categoria() == null && dto.metodo() == null && dto.valor() == null);
        if (seTipoECategoriaNaoENull) {
            if (seTipoOuCategoriaENull) {
                throw new IllegalArgumentException("Para atualizar 'tipo' ou 'categoria' ou 'metodo' ou 'valor' todos tem que ser enviados!");
            }
        }
    }

    private void validaSeDataInicialMenorIgualFinal(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("A data inicial '%s' deve ser menor ou igual a data final '%s'.".formatted(dataInicial.format(formatoDataDDMMAAAA), dataFinal.format(formatoDataDDMMAAAA)));
        }
    }

    private void validaSeUsuarioExiste(Long usuarioId) {
        var seExisteUsuario = (usuarioRepository.existsById(usuarioId));

        if (!seExisteUsuario) {
            throw new IllegalArgumentException("O usuário com ID '%s' não foi encontrado!".formatted(usuarioId));
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

        if (seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa) {
            throw new IllegalArgumentException(mensagem);
        } else if (seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private void validaSeMetodoAdequadoComTipoParaCadastro(TipoTransacao tipo, Metodo metodo) {
        var seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa = (tipo == TipoTransacao.DESPESA && !metodo.getTipo().equals(TipoTransacao.DESPESA));
        var seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita = (tipo == TipoTransacao.RECEITA && !metodo.getTipo().equals(TipoTransacao.RECEITA));
        var mensagem = "Metodo %s é inválido para tipo %s.".formatted(metodo, tipo);

        if (seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa) {
            throw new IllegalArgumentException(mensagem);
        } else if (seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private void validaSeValorAdequadoComTipoParaCadastro(TipoTransacao tipo, BigDecimal valorAtual) {
        var seTipoTransacaoReceita = tipo.equals(TipoTransacao.RECEITA);
        var seValorAtualPositivo = valorAtual.signum() == 1;
        var seValorAtualZero = valorAtual.signum() == 0;
        var seTipoTransacaoReceitaEValorAtualPositivo = (seTipoTransacaoReceita == seValorAtualPositivo);
        var mensagem = "O 'valor' %.2f não é apropriado para o 'tipo' %s.".formatted(valorAtual, tipo);

        if (!seTipoTransacaoReceitaEValorAtualPositivo || seValorAtualZero) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private LocalDateTime converteParaLocalDateTime(String data, boolean finalDia) {
        data = getData(data, finalDia);

        var horario = (finalDia) ? " 23:59:59" : " 00:00:00";

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(data + horario, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            return localDateTime;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("A data informada '%s' não está no formato válido 'dd/MM/AAAA'.".formatted(data));
        }
    }

    private String getData(String dataTratada, boolean finalDia) {
        if (dataTratada == null) {
            if (finalDia) {
                dataTratada = LocalDate.now().format(formatoDataDDMMAAAA);
            } else {
                dataTratada = "01/01/1900";
            }
        }
        return dataTratada;
    }

    private BigDecimal getSaldo(Usuario usuario, LocalDateTime dataIncial, LocalDateTime dataFinal) {
        var saldoTratado = BigDecimal.ZERO;
        var saldoAnterior = repositoryTransacaoComSaldo.findTopByUsuarioAndDataHoraTransacaoBetweenOrderByDataHoraTransacaoDescIdDesc(usuario, dataIncial, dataFinal);
        if (saldoAnterior.isPresent()) {
            saldoTratado = saldoAnterior.get().getSaldo();
        }

        return saldoTratado;
    }

    private TransacaoDadosAposConsultaComSaldoDeUsuarioPorPeriodoDTO getTransacaoDadosAposConsultaComSaldoDeUsuarioPorPeriodoDTO(Pageable paginacao, Long usuarioId, String dataInicial, String dataFinal, Usuario usuario) {
        var dataAtualInicial = converteParaLocalDateTime(dataInicial, false);
        var dataAtualFinal = converteParaLocalDateTime(dataFinal, true);

        validaSeDataInicialMenorIgualFinal(dataAtualInicial.toLocalDate(), dataAtualFinal.toLocalDate());
        var listaPaginada = repositoryTransacaoComSaldo.findAllByUsuarioAndDataHoraTransacaoBetween(paginacao, usuario, dataAtualInicial, dataAtualFinal).map(TransacaoComSaldoDadosListagemDTO::new);
        var saldoAtual = getSaldo(usuario, dataAtualInicial, dataAtualFinal);

        var dataAnteriorInicial = converteParaLocalDateTime(null, false);
        var dataAnteriorFinal = dataAtualInicial.minusSeconds(1L);
        var saldoAnterior = getSaldo(usuario, dataAnteriorInicial, dataAnteriorFinal);

        var resposta = new TransacaoDadosAposConsultaComSaldoDeUsuarioPorPeriodoDTO(dataAtualInicial.toLocalDate().format(formatoDataDDMMAAAA), dataAtualFinal.toLocalDate().format(formatoDataDDMMAAAA), saldoAnterior, saldoAtual, usuarioId, listaPaginada);
        return resposta;
    }
}

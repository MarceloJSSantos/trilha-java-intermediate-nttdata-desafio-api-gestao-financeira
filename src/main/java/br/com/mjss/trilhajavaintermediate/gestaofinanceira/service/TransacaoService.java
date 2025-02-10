package br.com.mjss.trilhajavaintermediate.gestaofinanceira.service;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.*;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.transacao.*;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.model.usuario.Usuario;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoComSaldoViewRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.TransacaoRepository;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository repository;

    @Autowired
    private TransacaoComSaldoViewRepository repositoryTransacaoComSaldo;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Transacao cadastrar(TransacaoCadastroDTO dto){
        var usuario = usuarioRepository.getReferenceById(dto.idUsuario());
        var transacao = new Transacao(usuario, dto);

        validaSeUsuarioExiste(dto.idUsuario());
        validaSeMetodoAdequadoComTipo(dto.tipo(), dto.metodo());
        validaSeCategoriaAdequadaComTipo(dto.tipo(), dto.categoria());
        validaSeValorAdequadoComTipo(dto.valor(), dto.tipo());

        return repository.save(transacao);
    }

    public TransacaoDadosAposCadastroOuAtualizacaoDTO consultarTransacao(Long id){
        try{
            var transacao = repository.getReferenceById(id);
            var dto = new TransacaoDadosAposCadastroOuAtualizacaoDTO(transacao);
            return dto;
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("A transação com ID '%s' não foi encontrado!".formatted(id));
        }
    }

    public void excluirTransacao(Long id){
        if (repository.existsById(id))
            repository.deleteById(id);
        else {
            throw new EntityNotFoundException("A transação com ID '%s' não foi encontrado!".formatted(id));
        }
    }

    public TransacaoDadosAposConsultaComSaldoDeUsuarioPorPeriodoDTO listarTransacoesComSaldoDeUsuarioPorPeriodo(Pageable paginacao, Long usuarioId, String dataInicial, String dataFinal) {
        if (usuarioRepository.existsById(usuarioId)){
            var usuario = usuarioRepository.getReferenceById(usuarioId);
            var dataAtualInicial = converteParaLocalDateTime(dataInicial, false);
            var dataAtualFinal = converteParaLocalDateTime(dataFinal, true);
            var listaPaginada = repositoryTransacaoComSaldo.findAllByUsuarioAndDataHoraTransacaoBetween(paginacao, usuario, dataAtualInicial, dataAtualFinal)
                    .map(TransacaoComSaldoDadosListagemDTO::new);
            var dataAnteriorInicial = converteParaLocalDateTime("01/01/1900", false);
            var dataAnteriorFinal = dataAtualInicial.minusSeconds(1L);
            var saldoAnterior = getSaldo(usuario, dataAnteriorInicial, dataAnteriorFinal);
            var saldoAtual = getSaldo(usuario, dataAtualInicial, dataAtualFinal);
            var resposta = new TransacaoDadosAposConsultaComSaldoDeUsuarioPorPeriodoDTO(dataInicial, dataFinal, saldoAnterior, saldoAtual, usuarioId, listaPaginada );
            return resposta;
        } else{
            throw new EntityNotFoundException("O usuário com ID '%s' não foi encontrado!".formatted(usuarioId));
        }
    }

    public Transacao atualizarTransacao(@Valid TransacaoAtualizacaoDTO dto) {
        final String MENSAGEM_EXCEPTION = "Para atualizar 'tipo' ou 'categoria' ou 'metodo' ou 'valor' todos tem que ser enviados!";
        try {
            var transacao = repository.getReferenceById(dto.id());

            var seTipoOuCategoriaENull = (dto.tipo() == null || dto.categoria() == null);
            var seTipoECategoriaNaoENull = !(dto.tipo() == null && dto.categoria() == null);
            if(seTipoECategoriaNaoENull){
                if(seTipoOuCategoriaENull){
                    throw new IllegalArgumentException(MENSAGEM_EXCEPTION);
                }
            }

            var seTipoOuMetodoENull = (dto.tipo() == null || dto.metodo() == null);
            var seTipoEMetodoNaoENull = !(dto.tipo() == null && dto.metodo() == null);
            if (seTipoEMetodoNaoENull){
                if(seTipoOuMetodoENull){
                    throw new IllegalArgumentException(MENSAGEM_EXCEPTION);
                }
            }

            var seTipoOuValorENull = (dto.tipo() == null || dto.valor() == null);
            var seTipoEValorNaoENull = !(dto.tipo() == null && dto.valor() == null);
            if(seTipoEValorNaoENull){
                if(seTipoOuValorENull){
                    throw new IllegalArgumentException(MENSAGEM_EXCEPTION);
                }
            }

            var seTipoECategoriaNaoSaoNull = (dto.tipo() != null && dto.categoria() != null);
            if (seTipoECategoriaNaoSaoNull){
                validaSeCategoriaAdequadaComTipo(dto.tipo(), dto.categoria());
            }

            var seTipoEMetodoNaoSaoNull = (dto.tipo() != null && dto.metodo() != null);
            if (seTipoEMetodoNaoSaoNull){
                validaSeMetodoAdequadoComTipo(dto.tipo(), dto.metodo());
            }

            var seTipoEValorNaoSaoNull = (dto.tipo() != null && dto.valor() != null);
            if(seTipoEValorNaoSaoNull){
                validaSeValorAdequadoComTipo(dto.valor(), dto.tipo());
            }

            transacao.atualizarTransacao(dto);

            return transacao;
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("A transação com ID '%s' não foi encontrado!".formatted(dto.id()));
        }
    }

    private void validaSeUsuarioExiste(Long idUsuario) {
        var seExisteUsuario = (usuarioRepository.existsById(idUsuario));

        if (!seExisteUsuario){
            throw new IllegalArgumentException("O ID %d não corresponde a nenhum usuário.".formatted(idUsuario));
        }
    }

    private void validaSeCategoriaAdequadaComTipo(TipoTransacao tipo, Categoria categoria){
        var seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa = (tipo == TipoTransacao.DESPESA && !categoria.getTipo().equals(TipoTransacao.DESPESA));
        var seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita = (tipo == TipoTransacao.RECEITA && !categoria.getTipo().equals(TipoTransacao.RECEITA));
        var mensagem = "Categoria %s é inválida para tipo %s.".formatted(categoria, tipo);

        if (seTipoTransacaoEDespesaEseTipoDaCategoriaNaoEDespesa) {
            throw new IllegalArgumentException(mensagem);
        } else if (seTipoTransacaoEReceitaEseTipoDaCategoriaNaoEReceita) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private void validaSeMetodoAdequadoComTipo(TipoTransacao tipo, Metodo metodo){
        var seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa = (tipo == TipoTransacao.DESPESA && !metodo.getTipo().equals(TipoTransacao.DESPESA));
        var seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita = (tipo == TipoTransacao.RECEITA && !metodo.getTipo().equals(TipoTransacao.RECEITA));
        var mensagem = "Metodo %s é inválido para tipo %s.".formatted(metodo, tipo);

        if (seTipoTransacaoEDespesaEseTipoDoMetodoNaoEDespesa) {
            throw new IllegalArgumentException(mensagem);
        } else if (seTipoTransacaoEReceitaEseTipoDoMetodoNaoEReceita) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private void validaSeValorAdequadoComTipo(BigDecimal valorAtual, TipoTransacao tipo){
        var seTipoTransacaoReceita = tipo.equals(TipoTransacao.RECEITA);
        var seValorAtualPositivo = valorAtual.signum() == 1;
        var seValorAtualZero = valorAtual.signum() == 0;
        var seTipoTransacaoReceitaEValorAtualPositivo = (seTipoTransacaoReceita == seValorAtualPositivo);
        var mensagem = "O 'valor' %.2f não é apropriado para o 'tipo' %s.".formatted(valorAtual, tipo);

        if (!seTipoTransacaoReceitaEValorAtualPositivo || seValorAtualZero){
            throw new IllegalArgumentException(mensagem);
        }
    }

    private LocalDateTime converteParaLocalDateTime(String dateString, boolean finalDia) {
        var horario = (finalDia)?" 23:59:59":" 00:00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateString + horario, DateTimeFormatter
                    .ofPattern("dd/MM/yyyy HH:mm:ss"));
            return localDateTime;
        } catch (DateTimeParseException e) {
            System.err.println("Erro ao parsear a data: " + e.getMessage());
            return null;
        }
    }

    private BigDecimal getSaldo(Usuario usuario, LocalDateTime dataIncial, LocalDateTime dataFinal){
        var retornoTratado = BigDecimal.ZERO;
        var saldoAnterior = repositoryTransacaoComSaldo.findTopByUsuarioAndDataHoraTransacaoBetweenOrderByDataHoraTransacaoDescIdDesc(usuario, dataIncial, dataFinal);
        if (saldoAnterior.isPresent()){
            retornoTratado = saldoAnterior.get().getSaldo();
        }

        return retornoTratado;
    }
}

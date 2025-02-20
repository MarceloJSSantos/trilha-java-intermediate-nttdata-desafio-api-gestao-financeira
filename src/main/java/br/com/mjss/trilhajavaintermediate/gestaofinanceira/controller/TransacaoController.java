package br.com.mjss.trilhajavaintermediate.gestaofinanceira.controller;

import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.*;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.dto.transacao.resumo.ResumoTransacaoDeUsuarioPorPeriodoDTO;
import br.com.mjss.trilhajavaintermediate.gestaofinanceira.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService service;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid TransacaoCadastroDTO dto, UriComponentsBuilder uriBuilder){
        var transacao = service.cadastrar(dto);
        var uri = uriBuilder.path("/transacoes/{id}").buildAndExpand(transacao.getId()).toUri();
        return ResponseEntity.created(uri).body(new TransacaoDadosAposCadastroOuAtualizacaoDTO(transacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity consultarTransacao(@PathVariable Long id){
        var respostaDTO = service.consultarTransacao(id);
        return ResponseEntity.ok(respostaDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluirTransacao(@PathVariable Long id){
        service.excluirTransacao(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listar_com_saldo_por_periodo/{usuarioId}")
    public ResponseEntity<TransacaoDadosComSaldoDeUsuarioPorPeriodoDTO> listarTransacoesComSaldoDeUsuarioPorPeriodo(
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long usuarioId){
        Pageable paginacao = PageRequest.of(page, size, Sort.by("dataHoraTransacao", "id").descending());
        var respotaPaginada = service.listarTransacoesComSaldoDeUsuarioPorPeriodo(paginacao, usuarioId, dataInicial, dataFinal);
        return ResponseEntity.ok(respotaPaginada);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizarTransacao(@RequestBody @Valid TransacaoAtualizacaoDTO dto){
        var transacao = service.atualizarTransacao(dto);
        return ResponseEntity.ok(new TransacaoDadosAposCadastroOuAtualizacaoDTO(transacao));
    }

    @GetMapping("/resumir_por_tipo_categorias_periodo/{usuarioId}")
    public ResponseEntity<ResumoTransacaoDeUsuarioPorPeriodoDTO> ResumoDeUsuarioPorPeriodoETipoECategoria(
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal,
            @PathVariable Long usuarioId){
        var resposta = service.ResumoDeUsuarioPorPeriodoETipoECategoria(usuarioId, dataInicial, dataFinal);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/resumir_por_tipo_metodos_periodo/{usuarioId}")
    public ResponseEntity<ResumoTransacaoDeUsuarioPorPeriodoDTO> ResumoDeUsuarioPorPeriodoETipoEMetodo(
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal,
            @PathVariable Long usuarioId){
        var resposta = service.ResumoDeUsuarioPorPeriodoETipoEMetodo(usuarioId, dataInicial, dataFinal);
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/cadastrar_transacoes_por_planilha/{usuarioId}")
    @Transactional
    public void cadastrarTransacaoDePlanilhaExcel(@RequestParam("arquivo") MultipartFile arquivo,
                                                  @PathVariable Long usuarioId) {
        service.cadastrarTransacaoDePlanilhaExcel(arquivo,usuarioId);
    }
}

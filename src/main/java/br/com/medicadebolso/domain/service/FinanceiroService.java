package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.dto.ExtratoItemDTO;
import br.com.medicadebolso.domain.dto.ResgateResponseDTO;
import br.com.medicadebolso.domain.dto.SolicitarResgateDTO;
import br.com.medicadebolso.domain.exception.MedicoNaoEncontradoException;
import br.com.medicadebolso.domain.exception.SaldoInsuficienteException;
import br.com.medicadebolso.domain.model.Medico;
import br.com.medicadebolso.domain.model.Pagamento;
import br.com.medicadebolso.domain.model.Resgate;
import br.com.medicadebolso.domain.repository.MedicoRepository;
import br.com.medicadebolso.domain.repository.PagamentoRepository;
import br.com.medicadebolso.domain.repository.ResgateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinanceiroService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ResgateRepository resgateRepository;

    @Transactional(readOnly = true)
    public BigDecimal getSaldoDisponivel(Long medicoId) {
        validarMedicoExiste(medicoId);
        return pagamentoRepository.findSaldoDisponivelByMedicoId(medicoId, Pagamento.StatusDisponibilidade.DISPONIVEL)
                .orElse(BigDecimal.ZERO);
    }

    @Transactional
    public ResgateResponseDTO solicitarResgate(SolicitarResgateDTO dto) {
        validarMedicoExiste(dto.getMedicoId());
        
        BigDecimal saldoDisponivel = getSaldoDisponivel(dto.getMedicoId());
        if (saldoDisponivel.compareTo(dto.getValor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o resgate");
        }

        Resgate resgate = new Resgate();
        resgate.setMedico(medicoRepository.findById(dto.getMedicoId()).get());
        resgate.setValorTotal(dto.getValor());
        resgate.setStatus(Resgate.StatusResgate.PENDENTE);

        // Buscar pagamentos disponíveis para o resgate
        List<Pagamento> pagamentosDisponiveis = pagamentoRepository
                .findByConsultaMedicoIdAndStatusDisponibilidade(
                        dto.getMedicoId(),
                        Pagamento.StatusDisponibilidade.DISPONIVEL
                );

        // Selecionar pagamentos até atingir o valor solicitado
        List<Pagamento> pagamentosSelecionados = new ArrayList<>();
        BigDecimal valorAcumulado = BigDecimal.ZERO;
        
        for (Pagamento pagamento : pagamentosDisponiveis) {
            if (valorAcumulado.compareTo(dto.getValor()) < 0) {
                pagamentosSelecionados.add(pagamento);
                valorAcumulado = valorAcumulado.add(pagamento.getValorLiquido());
            } else {
                break;
            }
        }

        // Atualizar status dos pagamentos selecionados
        for (Pagamento pagamento : pagamentosSelecionados) {
            pagamento.setStatusDisponibilidade(Pagamento.StatusDisponibilidade.RESGATADO);
            pagamento.setResgate(resgate);
        }

        resgate.setPagamentosIncluidos(pagamentosSelecionados);
        resgate = resgateRepository.save(resgate);

        return mapToResgateResponseDTO(resgate);
    }

    @Transactional(readOnly = true)
    public List<ExtratoItemDTO> getExtrato(Long medicoId, LocalDateTime inicio, LocalDateTime fim) {
        validarMedicoExiste(medicoId);

        List<ExtratoItemDTO> extrato = new ArrayList<>();

        // Adicionar pagamentos ao extrato
        List<Pagamento> pagamentos = pagamentoRepository
                .findByConsultaMedicoIdAndStatusPagamentoAndDataPagamentoBetween(
                        medicoId,
                        Pagamento.StatusPagamento.CONCLUIDO,
                        inicio,
                        fim
                );

        extrato.addAll(pagamentos.stream()
                .map(this::mapPagamentoToExtratoItem)
                .collect(Collectors.toList()));

        // Adicionar resgates ao extrato
        List<Resgate> resgates = resgateRepository
                .findByMedicoIdAndDataSolicitacaoBetween(medicoId, inicio, fim);

        extrato.addAll(resgates.stream()
                .map(this::mapResgateToExtratoItem)
                .collect(Collectors.toList()));

        // Ordenar por data
        extrato.sort((a, b) -> b.getData().compareTo(a.getData()));

        return extrato;
    }

    private void validarMedicoExiste(Long medicoId) {
        if (!medicoRepository.existsById(medicoId)) {
            throw new MedicoNaoEncontradoException(medicoId);
        }
    }

    private ResgateResponseDTO mapToResgateResponseDTO(Resgate resgate) {
        ResgateResponseDTO dto = new ResgateResponseDTO();
        dto.setId(resgate.getId());
        dto.setValor(resgate.getValorTotal());
        dto.setStatus(resgate.getStatus());
        dto.setDataSolicitacao(resgate.getDataSolicitacao());
        dto.setDataProcessamento(resgate.getDataProcessamento());
        dto.setMensagem("Resgate solicitado com sucesso");
        return dto;
    }

    private ExtratoItemDTO mapPagamentoToExtratoItem(Pagamento pagamento) {
        ExtratoItemDTO dto = new ExtratoItemDTO();
        dto.setTipo("PAGAMENTO");
        dto.setData(pagamento.getDataPagamento());
        dto.setValor(pagamento.getValorBruto());
        dto.setValorLiquido(pagamento.getValorLiquido());
        dto.setStatus(pagamento.getStatus().name());
        dto.setMetodoPagamento(pagamento.getMetodoPagamento());
        dto.setDescricao("Pagamento da consulta #" + pagamento.getConsulta().getId());
        return dto;
    }

    private ExtratoItemDTO mapResgateToExtratoItem(Resgate resgate) {
        ExtratoItemDTO dto = new ExtratoItemDTO();
        dto.setTipo("RESGATE");
        dto.setData(resgate.getDataSolicitacao());
        dto.setValor(resgate.getValorTotal());
        dto.setStatus(resgate.getStatus().name());
        dto.setDescricao("Resgate #" + resgate.getId());
        return dto;
    }
} 
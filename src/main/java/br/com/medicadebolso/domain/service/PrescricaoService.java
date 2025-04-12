package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.dto.PrescricaoDTO;
import br.com.medicadebolso.domain.exception.ConsultaNaoEncontradaException;
import br.com.medicadebolso.domain.exception.MedicoNaoEncontradoException;
import br.com.medicadebolso.domain.model.*;
import br.com.medicadebolso.domain.repository.ConsultaRepository;
import br.com.medicadebolso.domain.repository.MedicoRepository;
import br.com.medicadebolso.domain.repository.PrescricaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescricaoService {
    
    @Autowired
    private PrescricaoRepository prescricaoRepository;
    
    @Autowired
    private ConsultaRepository consultaRepository;
    
    @Autowired
    private MedicoRepository medicoRepository;
    
    @Transactional
    public Prescricao criarPrescricao(PrescricaoDTO prescricaoDTO) {
        Long consultaId = prescricaoDTO.getConsultaId();
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ConsultaNaoEncontradaException(consultaId));
                
        Long medicoId = prescricaoDTO.getMedicoId();
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new MedicoNaoEncontradoException(medicoId));
        
        Prescricao prescricao = new Prescricao();
        prescricao.setConsulta(consulta);
        prescricao.setMedico(medico);
        prescricao.setObservacoes(prescricaoDTO.getObservacoes());
        
        List<ItemPrescricao> itens = prescricaoDTO.getItens().stream()
                .map(itemDTO -> {
                    ItemPrescricao item = new ItemPrescricao();
                    item.setPrescricao(prescricao);
                    item.setMedicamento(itemDTO.getMedicamento());
                    item.setDosagem(itemDTO.getDosagem());
                    item.setViaAdministracao(itemDTO.getViaAdministracao());
                    item.setFrequencia(itemDTO.getFrequencia());
                    item.setDuracaoDias(itemDTO.getDuracaoDias());
                    item.setObservacoes(itemDTO.getObservacoes());
                    return item;
                })
                .collect(Collectors.toList());
        
        prescricao.setItens(itens);
        
        return prescricaoRepository.save(prescricao);
    }
    
    public List<Prescricao> buscarPrescricoesPorConsulta(Long consultaId) {
        return prescricaoRepository.findByConsultaId(consultaId);
    }
    
    public List<Prescricao> buscarPrescricoesPorMedico(Long medicoId) {
        return prescricaoRepository.findByMedicoId(medicoId);
    }
    
    public List<Prescricao> buscarPrescricoesPorPaciente(Long pacienteId) {
        return prescricaoRepository.findByConsultaPacienteId(pacienteId);
    }
} 
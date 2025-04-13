package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.dto.DadosBancariosDTO;
import br.com.medicadebolso.domain.dto.EnderecoDTO;
import br.com.medicadebolso.domain.dto.MedicoProfileDTO;
import br.com.medicadebolso.domain.exception.ResourceNotFoundException; // Assumindo que temos essa exception
import br.com.medicadebolso.domain.model.DadosBancarios;
import br.com.medicadebolso.domain.model.Endereco;
import br.com.medicadebolso.domain.model.Medico;
import br.com.medicadebolso.domain.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    // Poderíamos injetar um Mapper aqui se criarmos um dedicado

    @Autowired
    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @Transactional(readOnly = true) // Boa prática para métodos de leitura
    public MedicoProfileDTO getMedicoProfile(Long medicoId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com ID: " + medicoId));
        
        // TODO: Implementar lógica de mapeamento Medico -> MedicoProfileDTO
        return mapToMedicoProfileDTO(medico); 
    }

    @Transactional // Transação de escrita
    public MedicoProfileDTO updateMedicoProfile(Long medicoId, MedicoProfileDTO medicoProfileDTO) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com ID: " + medicoId));

        // TODO: Implementar lógica de mapeamento MedicoProfileDTO -> Medico (atualização)
        mapToMedicoEntity(medicoProfileDTO, medico);

        Medico medicoAtualizado = medicoRepository.save(medico);
        
        // TODO: Mapear o médico atualizado de volta para DTO para retorno
        return mapToMedicoProfileDTO(medicoAtualizado); 
    }

    // --- Métodos de Mapeamento (Exemplo) ---

    private MedicoProfileDTO mapToMedicoProfileDTO(Medico medico) {
        MedicoProfileDTO dto = new MedicoProfileDTO();
        dto.setId(medico.getId());
        dto.setNome(medico.getNome());
        dto.setEmail(medico.getEmail());
        dto.setTelefone(medico.getTelefone());
        dto.setCrmNumero(medico.getCrmNumero());
        dto.setCrmEstado(medico.getCrmEstado());
        dto.setProfilePicture(medico.getProfilePicture());
        dto.setRating(medico.getRating());
        dto.setEspecialidades(medico.getEspecialidades());
        dto.setValorConsulta(medico.getValorConsulta());

        if (medico.getEndereco() != null) {
            dto.setEndereco(mapToEnderecoDTO(medico.getEndereco()));
        }
        if (medico.getDadosBancarios() != null) {
            dto.setDadosBancarios(mapToDadosBancariosDTO(medico.getDadosBancarios()));
        }
        
        return dto;
    }

    private EnderecoDTO mapToEnderecoDTO(Endereco endereco) {
        EnderecoDTO dto = new EnderecoDTO();
        dto.setCep(endereco.getCep());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setNumero(endereco.getNumero());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setPontoReferencia(endereco.getPontoReferencia());
        dto.setCidade(endereco.getCidade());
        dto.setUf(endereco.getUf());
        return dto;
    }

    private DadosBancariosDTO mapToDadosBancariosDTO(DadosBancarios dadosBancarios) {
        DadosBancariosDTO dto = new DadosBancariosDTO();
        dto.setRazaoSocial(dadosBancarios.getRazaoSocial());
        dto.setCnpj(dadosBancarios.getCnpj());
        dto.setCodigoBanco(dadosBancarios.getCodigoBanco());
        dto.setNomeBanco(dadosBancarios.getNomeBanco());
        dto.setAgencia(dadosBancarios.getAgencia());
        dto.setAgenciaDv(dadosBancarios.getAgenciaDv());
        dto.setConta(dadosBancarios.getConta());
        dto.setContaDv(dadosBancarios.getContaDv());
        dto.setTipoConta(dadosBancarios.getTipoConta());
        return dto;
    }

    private void mapToMedicoEntity(MedicoProfileDTO dto, Medico medico) {
        // Atualiza apenas os campos permitidos do MedicoProfileDTO
        // Campos básicos herdados de Usuario (geralmente não devem ser alterados aqui,
        // exceto talvez nome e telefone)
        medico.setNome(dto.getNome());
        medico.setTelefone(dto.getTelefone());
        // Email e CPF geralmente são imutáveis ou gerenciados em outro lugar

        // Campos específicos de Medico
        medico.setCrmNumero(dto.getCrmNumero());
        medico.setCrmEstado(dto.getCrmEstado());
        medico.setProfilePicture(dto.getProfilePicture());
        // Rating é calculado, não setado diretamente
        medico.setEspecialidades(dto.getEspecialidades());
        medico.setValorConsulta(dto.getValorConsulta());
        // Não alteramos 'certificateToken' aqui

        // Mapeia/Atualiza Endereco
        if (dto.getEndereco() != null) {
            if (medico.getEndereco() == null) {
                medico.setEndereco(new Endereco());
            }
            mapToEnderecoEntity(dto.getEndereco(), medico.getEndereco());
        } else {
            // Se o DTO não tem endereço, removemos o existente (se cascade/orphanRemoval estiver ok)
            medico.setEndereco(null); 
        }

        // Mapeia/Atualiza DadosBancarios
        if (dto.getDadosBancarios() != null) {
            if (medico.getDadosBancarios() == null) {
                medico.setDadosBancarios(new DadosBancarios());
            }
            mapToDadosBancariosEntity(dto.getDadosBancarios(), medico.getDadosBancarios());
        } else {
             medico.setDadosBancarios(null);
        }
    }

    private void mapToEnderecoEntity(EnderecoDTO dto, Endereco endereco) {
        endereco.setCep(dto.getCep());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setPontoReferencia(dto.getPontoReferencia());
        endereco.setCidade(dto.getCidade());
        endereco.setUf(dto.getUf());
    }

    private void mapToDadosBancariosEntity(DadosBancariosDTO dto, DadosBancarios dadosBancarios) {
        dadosBancarios.setRazaoSocial(dto.getRazaoSocial());
        dadosBancarios.setCnpj(dto.getCnpj());
        dadosBancarios.setCodigoBanco(dto.getCodigoBanco());
        dadosBancarios.setNomeBanco(dto.getNomeBanco());
        dadosBancarios.setAgencia(dto.getAgencia());
        dadosBancarios.setAgenciaDv(dto.getAgenciaDv());
        dadosBancarios.setConta(dto.getConta());
        dadosBancarios.setContaDv(dto.getContaDv());
        dadosBancarios.setTipoConta(dto.getTipoConta());
    }
} 
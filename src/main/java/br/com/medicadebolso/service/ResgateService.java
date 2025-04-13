package br.com.medicadebolso.service;

import br.com.medicadebolso.domain.Resgate;
import br.com.medicadebolso.domain.Mensagem;
import br.com.medicadebolso.domain.enums.StatusResgate;
import br.com.medicadebolso.repository.ResgateRepository;
import br.com.medicadebolso.repository.MensagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResgateService {

    private final ResgateRepository resgateRepository;
    private final MensagemRepository mensagemRepository;

    @Transactional
    public Resgate solicitarResgate(Resgate resgate) {
        resgate.setStatus(StatusResgate.SOLICITADO);
        resgate.setDataSolicitacao(LocalDateTime.now());
        Resgate resgateSalvo = resgateRepository.save(resgate);

        // Criar mensagem de notificação
        Mensagem mensagem = new Mensagem();
        mensagem.setUsuarioId(resgate.getUsuarioId());
        mensagem.setTitulo("Solicitação de Resgate");
        mensagem.setConteudo("Sua solicitação de resgate foi recebida e está em análise.");
        mensagem.setLida(false);
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagemRepository.save(mensagem);

        return resgateSalvo;
    }

    @Transactional
    public Resgate processarResgate(Long id) {
        Resgate resgate = resgateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resgate não encontrado"));

        resgate.setStatus(StatusResgate.EM_PROCESSAMENTO);
        resgate.setDataProcessamento(LocalDateTime.now());
        Resgate resgateAtualizado = resgateRepository.save(resgate);

        // Criar mensagem de notificação
        Mensagem mensagem = new Mensagem();
        mensagem.setUsuarioId(resgate.getUsuarioId());
        mensagem.setTitulo("Resgate em Processamento");
        mensagem.setConteudo("Seu resgate está sendo processado.");
        mensagem.setLida(false);
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagemRepository.save(mensagem);

        return resgateAtualizado;
    }

    @Transactional
    public Resgate finalizarResgate(Long id) {
        Resgate resgate = resgateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resgate não encontrado"));

        resgate.setStatus(StatusResgate.PROCESSADO);
        resgate.setDataConclusao(LocalDateTime.now());
        Resgate resgateAtualizado = resgateRepository.save(resgate);

        // Criar mensagem de notificação
        Mensagem mensagem = new Mensagem();
        mensagem.setUsuarioId(resgate.getUsuarioId());
        mensagem.setTitulo("Resgate Concluído");
        mensagem.setConteudo("Seu resgate foi processado com sucesso.");
        mensagem.setLida(false);
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagemRepository.save(mensagem);

        return resgateAtualizado;
    }

    @Transactional
    public Resgate rejeitarResgate(Long id, String motivo) {
        Resgate resgate = resgateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resgate não encontrado"));

        resgate.setStatus(StatusResgate.REJEITADO);
        resgate.setMotivoRejeicao(motivo);
        resgate.setDataConclusao(LocalDateTime.now());
        Resgate resgateAtualizado = resgateRepository.save(resgate);

        // Criar mensagem de notificação
        Mensagem mensagem = new Mensagem();
        mensagem.setUsuarioId(resgate.getUsuarioId());
        mensagem.setTitulo("Resgate Rejeitado");
        mensagem.setConteudo("Seu resgate foi rejeitado. Motivo: " + motivo);
        mensagem.setLida(false);
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagemRepository.save(mensagem);

        return resgateAtualizado;
    }

    public List<Resgate> listarResgatesPorUsuario(Long usuarioId) {
        return resgateRepository.findByUsuarioIdOrderByDataSolicitacaoDesc(usuarioId);
    }
} 
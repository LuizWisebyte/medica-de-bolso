package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.model.Resgate;
import br.com.medicadebolso.domain.model.Mensagem;
import br.com.medicadebolso.domain.model.Resgate.StatusResgate;
import br.com.medicadebolso.domain.repository.ResgateRepository;
import br.com.medicadebolso.domain.repository.MensagemRepository;
import br.com.medicadebolso.domain.repository.UsuarioRepository;
import br.com.medicadebolso.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// @Service // Comentado temporariamente para isolar erros de inicialização
@RequiredArgsConstructor
public class ResgateService {

    private final ResgateRepository resgateRepository;
    private final MensagemRepository mensagemRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Resgate solicitarResgate(Resgate resgate) {
        Resgate resgateSalvo = resgateRepository.save(resgate);
        criarMensagemNotificacao(resgateSalvo, "Solicitação de Resgate", "Sua solicitação de resgate foi recebida e está em análise.");
        return resgateSalvo;
    }

    @Transactional
    public Resgate processarResgate(Long id) {
        Resgate resgate = buscarResgatePorId(id);
        resgate.setStatus(StatusResgate.PROCESSANDO);
        resgate.setDataProcessamento(LocalDateTime.now());
        Resgate resgateAtualizado = resgateRepository.save(resgate);
        criarMensagemNotificacao(resgateAtualizado, "Resgate em Processamento", "Seu resgate está sendo processado.");
        return resgateAtualizado;
    }

    @Transactional
    public Resgate finalizarResgate(Long id) {
        Resgate resgate = buscarResgatePorId(id);
        resgate.setStatus(StatusResgate.CONCLUIDO);
        resgate.setDataProcessamento(resgate.getDataProcessamento() != null ? resgate.getDataProcessamento() : LocalDateTime.now());
        Resgate resgateAtualizado = resgateRepository.save(resgate);
        criarMensagemNotificacao(resgateAtualizado, "Resgate Concluído", "Seu resgate foi processado com sucesso.");
        return resgateAtualizado;
    }

    @Transactional
    public Resgate rejeitarResgate(Long id, String motivo) {
        Resgate resgate = buscarResgatePorId(id);
        resgate.setStatus(StatusResgate.FALHOU);
        resgate.setDetalhesTransacao("Rejeitado: " + motivo);
        resgate.setDataProcessamento(resgate.getDataProcessamento() != null ? resgate.getDataProcessamento() : LocalDateTime.now());
        Resgate resgateAtualizado = resgateRepository.save(resgate);
        criarMensagemNotificacao(resgateAtualizado, "Resgate Rejeitado", "Seu resgate foi rejeitado. Motivo: " + motivo);
        return resgateAtualizado;
    }

    public List<Resgate> listarResgatesPorMedico(Long medicoId) {
        return resgateRepository.findAll();
    }

    private Resgate buscarResgatePorId(Long id) {
        return resgateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resgate não encontrado com ID: " + id));
    }

    private void criarMensagemNotificacao(Resgate resgate, String titulo, String conteudo) {
        if (resgate.getMedico() == null) {
            System.err.println("Erro: Resgate sem médico associado. ID Resgate: " + resgate.getId());
            return;
        }
        Usuario destinatario = resgate.getMedico();

        Mensagem mensagem = new Mensagem();
        mensagem.setRemetente(null);
        mensagem.setAtendimento(null);
        mensagem.setConteudo(conteudo);
        mensagemRepository.save(mensagem);
    }
} 
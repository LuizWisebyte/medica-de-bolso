import api from './api';

// --- Tipos de Atendimento --- 

// TODO: Alinhar com os DTOs do backend (AtendimentoController)

// Tipo para Atendimento (igual ao definido em userService antes)
export interface Atendimento {
    id: number;
    pacienteNome: string; 
    status: string; // Ex: AGUARDANDO, EM_ANDAMENTO, FINALIZADO, CANCELADO
    dataHoraInicio?: string; 
    tempoRestanteSegundos?: number;
    // ... outros dados relevantes do atendimento
}

// Tipo para Mensagem
export interface Mensagem {
    id: string; // Ou number, dependendo do backend (MongoDB geralmente usa string)
    atendimentoId: number;
    remetenteId: string; // ID do usuário (médico ou paciente)
    conteudo: string;
    dataHoraEnvio: string; // Formato ISO
    lida: boolean;
    tipo?: 'TEXTO' | 'IMAGEM'; // Exemplo
    urlImagem?: string; // Se for imagem
}

// --- Funções do Serviço --- 

/**
 * Busca a lista de atendimentos para o médico autenticado.
 * @returns Uma lista de atendimentos.
 * @throws Lança um erro se a requisição falhar.
 */
export const getMeusAtendimentos = async (): Promise<Atendimento[]> => {
    const rota = '/atendimentos/me'; // Rota hipotética (verificar backend)
    console.log(`[AtendimentoService] Buscando atendimentos em: ${rota}`);
    try {
        const response = await api.get<Atendimento[]>(rota);
        console.log('[AtendimentoService] Atendimentos recebidos com sucesso.');
        return response.data;
    } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'Erro ao buscar atendimentos';
        console.error('[AtendimentoService] Falha ao buscar atendimentos:', errorMessage, error.response?.data);
        throw new Error(errorMessage);
    }
};

/**
 * Inicia um atendimento específico.
 * @param atendimentoId - O ID do atendimento a ser iniciado.
 * @returns Os dados do atendimento atualizado.
 * @throws Lança um erro se a requisição falhar.
 */
export const iniciarAtendimento = async (atendimentoId: number): Promise<Atendimento> => {
    const rota = `/atendimentos/${atendimentoId}/iniciar`;
    console.log(`[AtendimentoService] Iniciando atendimento em: ${rota}`);
    try {
        const response = await api.post<Atendimento>(rota);
        console.log('[AtendimentoService] Atendimento iniciado com sucesso.');
        return response.data;
    } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'Erro ao iniciar atendimento';
        console.error('[AtendimentoService] Falha ao iniciar atendimento:', errorMessage, error.response?.data);
        throw new Error(errorMessage);
    }
};

/**
 * Busca o histórico de mensagens de um atendimento específico.
 * @param atendimentoId - O ID do atendimento.
 * @returns Uma lista de mensagens.
 * @throws Lança um erro se a requisição falhar.
 */
export const getMensagensPorAtendimento = async (atendimentoId: number): Promise<Mensagem[]> => {
    // Rota: /api/atendimentos/{atendimentoId}/mensagens (conforme AtendimentoController)
    const rota = `/atendimentos/${atendimentoId}/mensagens`;
    console.log(`[AtendimentoService] Buscando mensagens em: ${rota}`);
    try {
        const response = await api.get<Mensagem[]>(rota);
        console.log(`[AtendimentoService] Mensagens para atendimento ${atendimentoId} recebidas com sucesso.`);
        // Ordenar mensagens pela data de envio, se necessário (API pode já fazer isso)
        // response.data.sort((a, b) => new Date(a.dataHoraEnvio).getTime() - new Date(b.dataHoraEnvio).getTime());
        return response.data;
    } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'Erro ao buscar mensagens do atendimento';
        console.error(`[AtendimentoService] Falha ao buscar mensagens para atendimento ${atendimentoId}:`, errorMessage, error.response?.data);
        throw new Error(errorMessage);
    }
};

/**
 * Envia uma nova mensagem para um atendimento.
 * (Versão inicial REST - WebSocket virá depois)
 * @param atendimentoId - O ID do atendimento.
 * @param mensagemData - Objeto contendo os dados da mensagem (conteúdo, remetenteId - talvez inferido pelo backend?).
 * @returns A mensagem enviada (confirmada pela API).
 * @throws Lança um erro se a requisição falhar.
 */
// TODO: Definir tipo correto para envio (MensagemDTO do backend)
export interface EnviarMensagemData {
    conteudo: string;
    // remetenteId: string; // Backend deve saber pelo token?
    tipo?: 'TEXTO' | 'IMAGEM';
    urlImagem?: string;
}
export const enviarMensagemRest = async (atendimentoId: number, mensagemData: EnviarMensagemData): Promise<Mensagem> => {
     // Rota: /api/atendimentos/{atendimentoId}/mensagens (POST, conforme AtendimentoController)
    const rota = `/atendimentos/${atendimentoId}/mensagens`;
    console.log(`[AtendimentoService] Enviando mensagem REST para: ${rota}`);
    try {
        const response = await api.post<Mensagem>(rota, mensagemData);
        console.log(`[AtendimentoService] Mensagem REST enviada com sucesso para atendimento ${atendimentoId}.`);
        return response.data;
    } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'Erro ao enviar mensagem';
        console.error(`[AtendimentoService] Falha ao enviar mensagem REST para atendimento ${atendimentoId}:`, errorMessage, error.response?.data);
        throw new Error(errorMessage);
    }
};

// TODO: Adicionar finalizarAtendimento, cancelarAtendimento, etc. 
import api from './api';
import { UserProfile } from './userService'; // Pode ser útil para obter ID se /me não funcionar

// --- Tipos Financeiros --- 

// TODO: Alinhar com os DTOs do backend (FinanceiroController)

// Saldo retornado pela API (provavelmente um número)
export type Saldo = number; // ou string se a API retornar formatado

// Item individual do extrato
export interface ExtratoItem {
    id: number; // ou string
    dataHora: string; // Formato ISO
    tipo: 'ENTRADA' | 'SAIDA' | 'RESGATE_SOLICITADO' | 'RESGATE_PAGO'; // Exemplo
    descricao: string;
    valor: number;
    // atendimentoId?: number; // Opcional, se relacionado a uma consulta
}

// Parâmetros para buscar extrato (se necessário)
export interface ExtratoParams {
    inicio: string; // Formato ISO (YYYY-MM-DDTHH:mm:ss)
    fim: string;    // Formato ISO
}

// Dados para solicitar resgate
export interface SolicitarResgateData {
    valor: number;
    // O backend provavelmente já sabe a conta bancária associada ao médico
    // Se precisar especificar, adicionar campos aqui (banco, agencia, conta)
}

// Resposta da solicitação de resgate
export interface ResgateResponse {
    id: number; // ID da solicitação de resgate
    status: string; // Ex: SOLICITADO, PROCESSANDO, PAGO, FALHOU
    mensagem: string;
}

// --- Funções do Serviço --- 

/**
 * Busca o saldo disponível do médico autenticado.
 * @returns O valor do saldo.
 * @throws Lança um erro se a requisição falhar.
 */
export const getMeuSaldo = async (): Promise<Saldo> => {
    // Assumindo rota /api/financeiro/me/saldo
    // Alternativa: /api/financeiro/medico/{userId}/saldo (requer userId)
    const rota = '/financeiro/me/saldo'; 
    console.log(`[FinanceiroService] Buscando saldo em: ${rota}`);
    try {
        const response = await api.get<Saldo>(rota);
        console.log('[FinanceiroService] Saldo recebido com sucesso.');
        return response.data;
    } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'Erro ao buscar saldo';
        console.error('[FinanceiroService] Falha ao buscar saldo:', errorMessage, error.response?.data);
        throw new Error(errorMessage);
    }
};

/**
 * Busca o extrato financeiro do médico autenticado dentro de um período.
 * @param params - Objeto com datas de início e fim.
 * @returns Uma lista de itens do extrato.
 * @throws Lança um erro se a requisição falhar.
 */
export const getMeuExtrato = async (params: ExtratoParams): Promise<ExtratoItem[]> => {
    // Assumindo rota /api/financeiro/me/extrato com query params
    // Alternativa: /api/financeiro/medico/{userId}/extrato
    const rota = '/financeiro/me/extrato'; 
    console.log(`[FinanceiroService] Buscando extrato em: ${rota} com params:`, params);
    try {
        const response = await api.get<ExtratoItem[]>(rota, { params });
        console.log('[FinanceiroService] Extrato recebido com sucesso.');
        return response.data;
    } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'Erro ao buscar extrato';
        console.error('[FinanceiroService] Falha ao buscar extrato:', errorMessage, error.response?.data);
        throw new Error(errorMessage);
    }
};

/**
 * Solicita um resgate do saldo disponível.
 * @param data - Objeto contendo o valor a ser resgatado.
 * @returns A resposta da solicitação de resgate.
 * @throws Lança um erro se a requisição falhar.
 */
export const solicitarResgate = async (data: SolicitarResgateData): Promise<ResgateResponse> => {
    // Rota: /api/financeiro/resgate (conforme FinanceiroController)
    const rota = '/financeiro/resgate';
    console.log(`[FinanceiroService] Solicitando resgate em: ${rota} com dados:`, data);
    try {
        const response = await api.post<ResgateResponse>(rota, data);
        console.log('[FinanceiroService] Solicitação de resgate enviada com sucesso.');
        return response.data;
    } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'Erro ao solicitar resgate';
        console.error('[FinanceiroService] Falha ao solicitar resgate:', errorMessage, error.response?.data);
        throw new Error(errorMessage);
    }
}; 
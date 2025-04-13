import api from './api';

// TODO: Alinhar este tipo com o MedicoProfileDTO do backend
// Idealmente, este tipo seria compartilhado ou gerado a partir da definição da API
export interface UserProfile {
    id: string; // Ou number
    nome: string;
    email: string;
    crmNumber?: string;
    crmState?: string;
    profilePicture?: string;
    rating?: number;
    // ... outros campos do perfil
}

export type UpdateProfileData = Omit<Partial<UserProfile>, 'id' | 'email' | 'rating'>;

/**
 * Busca o perfil do usuário autenticado.
 * @returns Os dados do perfil do usuário.
 * @throws Lança um erro se a requisição falhar (ex: token inválido, erro no servidor).
 */
export const getUserProfile = async (): Promise<UserProfile> => {
    console.log(`[UserService] Buscando perfil do usuário em: /medicos/me`);
    try {
        const response = await api.get<UserProfile>('/medicos/me');
        console.log('[UserService] Perfil recebido com sucesso.');
        return response.data;
    } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'Erro ao buscar perfil do usuário';
        console.error('[UserService] Falha ao buscar perfil:', errorMessage, error.response?.data);
        // Se o erro for 401 (não autorizado), o interceptor de resposta em api.ts pode lidar com isso
        throw new Error(errorMessage);
    }
};

// TODO: Adicionar finalizarAtendimento, cancelarAtendimento, enviarMensagem, etc.

// TODO: Adicionar outras funções relacionadas ao usuário/médico se necessário
// (ex: updateProfile, etc.) 
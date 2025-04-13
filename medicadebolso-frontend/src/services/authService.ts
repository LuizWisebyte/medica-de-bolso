import api from './api'; // Importar a instância axios configurada

// TODO: Definir tipos mais específicos para os dados de entrada e respostas da API
// Exemplo de tipos (ajustar conforme API real):
interface LoginCredentials {
    email: string;
    senha: string; // Ou password, alinhar com API
}

interface RegisterData {
    nome: string;
    email: string;
    senha: string; // Ou password
    crm: string;
    crmEstado: string;
    // ... outros campos?
}

// Assumindo que a API retorna o token diretamente como string no corpo
// Se retornar um objeto { token: '...' }, ajustar o tipo de retorno e extração
type AuthResponse = string;

/**
 * Realiza a chamada de login para a API usando Axios.
 * @param credentials - Objeto contendo email e senha.
 * @returns O token JWT em caso de sucesso.
 * @throws Lança um erro em caso de falha na autenticação ou problema na requisição.
 */
export const login = async (credentials: LoginCredentials): Promise<AuthResponse> => {
    console.log(`[AuthService] Tentando login via Axios em: /auth/login`);
    try {
        const response = await api.post<AuthResponse>('/auth/login', credentials);
        console.log('[AuthService] Login via Axios bem-sucedido.');
        return response.data; // Axios retorna os dados diretamente em response.data
    } catch (error: any) {
        // Axios encapsula erros de forma diferente do fetch
        const errorMessage = error.response?.data?.message || error.message || 'Erro desconhecido no login';
        console.error('[AuthService] Falha no login via Axios:', errorMessage, error.response?.data);
        throw new Error(errorMessage);
    }
};

/**
 * Realiza a chamada de registro para a API usando Axios.
 * @param userData - Objeto contendo os dados do usuário para registro.
 * @returns O token JWT em caso de sucesso.
 * @throws Lança um erro em caso de falha no registro ou problema na requisição.
 */
export const register = async (userData: RegisterData): Promise<AuthResponse> => {
    console.log(`[AuthService] Tentando registro via Axios em: /auth/registro`);
    try {
        const response = await api.post<AuthResponse>('/auth/registro', userData);
        console.log('[AuthService] Registro via Axios bem-sucedido.');
        return response.data;
    } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'Erro desconhecido no registro';
        console.error('[AuthService] Falha no registro via Axios:', errorMessage, error.response?.data);
        throw new Error(errorMessage);
    }
};

// TODO: Adicionar outras funções relacionadas à autenticação se necessário (ex: logout, forgotPassword, etc.) 
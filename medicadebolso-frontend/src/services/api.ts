import axios, { AxiosError, AxiosResponse } from 'axios';
// import { parseCookies } from 'nookies'; // Removido - Usando localStorage por enquanto
// import { signOut } from '../contexts/AuthContext'; // Removido - Função não existe/evitar dependência circular

const API_BASE_URL = 'http://localhost:8090/api';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para adicionar o token JWT às requisições
api.interceptors.request.use(
  (config) => {
    // Tenta obter o token do localStorage (ou de onde quer que seja armazenado)
    const token = localStorage.getItem('authToken');
    
    // Adiciona o cabeçalho Authorization se o token existir
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('[Axios Interceptor] Token adicionado ao cabeçalho Authorization.');
    } else {
      console.log('[Axios Interceptor] Nenhum token encontrado para adicionar ao cabeçalho.');
    }
    
    return config;
  },
  (error) => {
    // Tratar erro na configuração da requisição
    console.error('[Axios Interceptor] Erro ao configurar requisição:', error);
    return Promise.reject(error);
  }
);

// Interceptor para tratar erros
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Ex: Limpar token e redirecionar para login
      console.error('[Axios Interceptor] Erro 401 - Não autorizado. Redirecionando para login...');
      localStorage.removeItem('authToken');
      // Idealmente, chamar a função logout do AuthContext, mas pode ser complexo aqui
      // window.location.href = '/login'; 
    }
    return Promise.reject(error);
  }
);

export const authService = {
  login: async (email: string, senha: string) => {
    const response = await api.post('/auth/login', { email, senha });
    return response.data;
  },
  register: async (userData: any) => {
    // Garantir que todos os campos obrigatórios estejam presentes
    if (!userData.tipoUsuario) {
      userData.tipoUsuario = 'MEDICO'; // Valor padrão se não for fornecido
    }
    const response = await api.post('/auth/registro', userData);
    return response.data;
  },
};

export const medicamentoService = {
  listar: async () => {
    const response = await api.get('/medicamentos');
    return response.data;
  },
  criar: async (medicamento: any) => {
    const response = await api.post('/medicamentos', medicamento);
    return response.data;
  },
  atualizar: async (id: string, medicamento: any) => {
    const response = await api.put(`/medicamentos/${id}`, medicamento);
    return response.data;
  },
  deletar: async (id: string) => {
    await api.delete(`/medicamentos/${id}`);
  },
};

export const mensagemService = {
  listar: async (usuarioId: string) => {
    const response = await api.get(`/mensagens/usuario/${usuarioId}`);
    return response.data;
  },
  listarNaoLidas: async (usuarioId: string) => {
    const response = await api.get(`/mensagens/usuario/${usuarioId}/nao-lidas`);
    return response.data;
  },
  contarNaoLidas: async (usuarioId: string) => {
    const response = await api.get(`/mensagens/usuario/${usuarioId}/contagem-nao-lidas`);
    return response.data;
  },
  marcarComoLida: async (mensagemId: string) => {
    await api.put(`/mensagens/${mensagemId}/marcar-como-lida`);
  },
};

export default api; 
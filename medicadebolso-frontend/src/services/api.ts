import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para adicionar token JWT
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor para tratar erros
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  login: async (email: string, password: string) => {
    const response = await api.post('/auth/authenticate', { email, password });
    return response.data;
  },
  register: async (nome: string, email: string, password: string) => {
    const response = await api.post('/auth/register', { nome, email, password });
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
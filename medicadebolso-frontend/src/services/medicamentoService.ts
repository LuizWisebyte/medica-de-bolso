import { api } from './api';
import { Medicamento } from '../types/medicamento';

export const medicamentoService = {
  listar: async () => {
    const response = await api.get<Medicamento[]>('/medicamentos');
    return response.data;
  },

  criar: async (medicamento: Omit<Medicamento, 'id' | 'createdAt' | 'updatedAt' | 'userId'>) => {
    const response = await api.post<Medicamento>('/medicamentos', medicamento);
    return response.data;
  },

  atualizar: async (id: string, medicamento: Partial<Medicamento>) => {
    const response = await api.put<Medicamento>(`/medicamentos/${id}`, medicamento);
    return response.data;
  },

  excluir: async (id: string) => {
    await api.delete(`/medicamentos/${id}`);
  },

  buscarPorId: async (id: string) => {
    const response = await api.get<Medicamento>(`/medicamentos/${id}`);
    return response.data;
  },
}; 
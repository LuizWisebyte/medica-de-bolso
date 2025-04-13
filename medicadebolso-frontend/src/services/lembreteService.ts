import { api } from './api';

export interface Lembrete {
  id: string;
  medicamentoId: string;
  horario: string;
  status: 'PENDENTE' | 'TOMADO' | 'ATRASADO';
  dataLembrete: string;
  createdAt: string;
  updatedAt: string;
}

export interface CriarLembreteDTO {
  medicamentoId: string;
  horario: string;
  dataLembrete: string;
}

export const lembreteService = {
  listarPorData: async (data: string) => {
    const response = await api.get<Lembrete[]>(`/lembretes/data/${data}`);
    return response.data;
  },

  listarPorMedicamento: async (medicamentoId: string) => {
    const response = await api.get<Lembrete[]>(`/lembretes/medicamento/${medicamentoId}`);
    return response.data;
  },

  criar: async (lembrete: CriarLembreteDTO) => {
    const response = await api.post<Lembrete>('/lembretes', lembrete);
    return response.data;
  },

  marcarComoTomado: async (lembreteId: string) => {
    const response = await api.patch<Lembrete>(`/lembretes/${lembreteId}/tomado`);
    return response.data;
  },

  excluir: async (lembreteId: string) => {
    await api.delete(`/lembretes/${lembreteId}`);
  },

  gerarLembretesAutomaticos: async (medicamentoId: string, dataInicio: string, dataFim?: string) => {
    const response = await api.post<Lembrete[]>(`/lembretes/gerar-automatico`, {
      medicamentoId,
      dataInicio,
      dataFim
    });
    return response.data;
  }
}; 
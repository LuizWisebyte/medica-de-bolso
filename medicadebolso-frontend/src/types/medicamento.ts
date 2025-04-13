// Definição unificada para Medicamento
export interface Medicamento {
  id: string;
  nome: string;
  dosagem: number;
  unidadeMedida: string;
  frequencia: string;
  dataInicio: string;
  dataTermino?: string;
  horarios: string[];
  observacoes?: string;
  userId: string;
  createdAt: string;
  updatedAt: string;
} 
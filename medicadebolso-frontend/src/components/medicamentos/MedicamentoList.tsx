import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { FiPlus, FiEdit2, FiTrash2 } from 'react-icons/fi';
import { medicamentoService } from '../../services/medicamentoService';
import MedicamentoForm, { MedicamentoFormData } from './MedicamentoForm';
import { Medicamento } from '../../types/medicamento';

interface MedicamentoListProps {
  medicamentos: Medicamento[];
  onUpdate: () => void;
}

export const MedicamentoList: React.FC<MedicamentoListProps> = ({ medicamentos, onUpdate }) => {
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [selectedMedicamento, setSelectedMedicamento] = useState<Medicamento | undefined>(undefined);

  const handleSubmit = async (medicamentoData: MedicamentoFormData) => {
    try {
      if (selectedMedicamento) {
        await medicamentoService.atualizar(selectedMedicamento.id, medicamentoData);
      } else {
        await medicamentoService.criar(medicamentoData);
      }
      onUpdate();
      setIsFormOpen(false);
      setSelectedMedicamento(undefined);
    } catch (error) {
      console.error('Erro ao salvar medicamento:', error);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      if (window.confirm('Tem certeza que deseja excluir este medicamento?')) {
        await medicamentoService.excluir(id);
        onUpdate();
      }
    } catch (error) {
      console.error('Erro ao excluir medicamento:', error);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-semibold">Meus Medicamentos</h2>
        <button
          onClick={() => {
            setSelectedMedicamento(undefined);
            setIsFormOpen(true);
          }}
          className="flex items-center gap-2 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          <FiPlus /> Novo Medicamento
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {medicamentos.map((medicamento) => (
          <motion.div
            key={medicamento.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="bg-white p-4 rounded-lg shadow"
          >
            <div className="flex justify-between items-start">
              <div>
                <h3 className="text-lg font-semibold">
                  {medicamento.nome}
                </h3>
                <p className="mt-1 text-sm text-gray-500">
                  {medicamento.dosagem} - {medicamento.frequencia}
                </p>
                <p className="mt-1 text-sm text-gray-500">
                  Horários: {medicamento.horarios.join(', ')}
                </p>
              </div>
              <div className="flex gap-2">
                <button
                  onClick={() => {
                    setSelectedMedicamento(medicamento);
                    setIsFormOpen(true);
                  }}
                  className="text-blue-500 hover:text-blue-600"
                >
                  <FiEdit2 size={20} />
                </button>
                <button
                  onClick={() => handleDelete(medicamento.id)}
                  className="text-red-500 hover:text-red-600"
                >
                  <FiTrash2 size={20} />
                </button>
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {isFormOpen && (
        <MedicamentoForm
          medicamento={selectedMedicamento}
          onSubmit={handleSubmit}
          onClose={() => {
            setIsFormOpen(false);
            setSelectedMedicamento(undefined);
          }}
        />
      )}
    </div>
  );
}; 
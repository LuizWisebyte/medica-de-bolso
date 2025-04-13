import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { FiPlus, FiEdit2, FiTrash2 } from 'react-icons/fi';
import { medicamentoService } from '../../services/api';
import MedicamentoForm from './MedicamentoForm';

interface Medicamento {
  id: string;
  nome: string;
  dosagem: string;
  frequencia: string;
  horarios: string[];
  dataInicio: string;
  dataFim?: string;
}

const MedicamentoList = () => {
  const [medicamentos, setMedicamentos] = useState<Medicamento[]>([]);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [selectedMedicamento, setSelectedMedicamento] = useState<Medicamento | null>(null);

  useEffect(() => {
    carregarMedicamentos();
  }, []);

  const carregarMedicamentos = async () => {
    const data = await medicamentoService.listar();
    setMedicamentos(data);
  };

  const handleSubmit = async (medicamento: Omit<Medicamento, 'id'>) => {
    if (selectedMedicamento) {
      await medicamentoService.atualizar(selectedMedicamento.id, medicamento);
    } else {
      await medicamentoService.criar(medicamento);
    }
    setIsFormOpen(false);
    setSelectedMedicamento(null);
    carregarMedicamentos();
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Tem certeza que deseja excluir este medicamento?')) {
      await medicamentoService.deletar(id);
      carregarMedicamentos();
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">Medicamentos</h2>
        <button
          onClick={() => {
            setSelectedMedicamento(null);
            setIsFormOpen(true);
          }}
          className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
        >
          <FiPlus className="-ml-1 mr-2 h-5 w-5" />
          Novo Medicamento
        </button>
      </div>

      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="bg-white shadow overflow-hidden sm:rounded-md"
      >
        <ul className="divide-y divide-gray-200">
          {medicamentos.map((medicamento) => (
            <motion.li
              key={medicamento.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="px-4 py-4 sm:px-6"
            >
              <div className="flex items-center justify-between">
                <div className="flex-1 min-w-0">
                  <h3 className="text-lg font-medium text-gray-900 truncate">
                    {medicamento.nome}
                  </h3>
                  <p className="mt-1 text-sm text-gray-500">
                    {medicamento.dosagem} - {medicamento.frequencia}
                  </p>
                  <p className="mt-1 text-sm text-gray-500">
                    Horários: {medicamento.horarios.join(', ')}
                  </p>
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => {
                      setSelectedMedicamento(medicamento);
                      setIsFormOpen(true);
                    }}
                    className="text-gray-400 hover:text-gray-500"
                  >
                    <FiEdit2 className="h-5 w-5" />
                  </button>
                  <button
                    onClick={() => handleDelete(medicamento.id)}
                    className="text-gray-400 hover:text-red-500"
                  >
                    <FiTrash2 className="h-5 w-5" />
                  </button>
                </div>
              </div>
            </motion.li>
          ))}
        </ul>
      </motion.div>

      {isFormOpen && (
        <MedicamentoForm
          medicamento={selectedMedicamento}
          onSubmit={handleSubmit}
          onClose={() => {
            setIsFormOpen(false);
            setSelectedMedicamento(null);
          }}
        />
      )}
    </div>
  );
};

export default MedicamentoList; 
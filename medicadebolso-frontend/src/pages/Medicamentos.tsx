import { useEffect, useState } from 'react';
import PrivateLayout from '../components/layouts/PrivateLayout';
import { api } from '../services/api';
import { Medicamento } from '../types/medicamento';
import { MedicamentoList } from '../components/medicamentos/MedicamentoList';
import MedicamentoForm from '../components/medicamentos/MedicamentoForm';

export default function Medicamentos() {
  const [medicamentos, setMedicamentos] = useState<Medicamento[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);

  const fetchMedicamentos = async () => {
    try {
      const response = await api.get('/medicamentos');
      setMedicamentos(response.data);
    } catch (err) {
      setError('Erro ao carregar medicamentos');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchMedicamentos();
  }, []);

  return (
    <PrivateLayout>
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Medicamentos</h1>
            <p className="mt-1 text-sm text-gray-600">
              Gerencie seus medicamentos e horários
            </p>
          </div>
          <button
            onClick={() => setIsFormOpen(true)}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            Novo Medicamento
          </button>
        </div>

        {isLoading ? (
          <div className="text-center">
            <p>Carregando...</p>
          </div>
        ) : error ? (
          <div className="bg-red-50 border-l-4 border-red-400 p-4">
            <div className="flex">
              <div className="flex-shrink-0">
                <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                </svg>
              </div>
              <div className="ml-3">
                <p className="text-sm text-red-700">{error}</p>
              </div>
            </div>
          </div>
        ) : (
          <MedicamentoList
            medicamentos={medicamentos}
            onUpdate={fetchMedicamentos}
          />
        )}

        {isFormOpen && (
          <MedicamentoForm
            onSubmit={async (medicamento) => {
              try {
                await api.post('/medicamentos', medicamento);
                fetchMedicamentos();
                setIsFormOpen(false);
              } catch (err) {
                console.error('Erro ao criar medicamento:', err);
              }
            }}
            onClose={() => setIsFormOpen(false)}
          />
        )}
      </div>
    </PrivateLayout>
  );
} 
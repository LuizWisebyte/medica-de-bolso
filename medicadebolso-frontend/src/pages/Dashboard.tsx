import { useEffect, useState } from 'react';
import PrivateLayout from '../components/layouts/PrivateLayout';
import { api } from '../services/api';
import { Medicamento } from '../types/medicamento';
import LembretesDoDia from '../components/lembretes/LembretesDoDia';
import { FiClock, FiCalendar, FiCheckCircle } from 'react-icons/fi';

export default function Dashboard() {
  const [medicamentos, setMedicamentos] = useState<Medicamento[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

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

  const estatisticas = {
    totalMedicamentos: medicamentos.length,
    medicamentosAtivos: medicamentos.filter(med => !med.dataTermino || new Date(med.dataTermino) >= new Date()).length,
    lembretesPendentes: medicamentos.reduce((total, med) => total + (med.horarios?.length || 0), 0)
  };

  return (
    <PrivateLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="mt-1 text-sm text-gray-600">
            Bem-vindo ao seu painel de controle de medicamentos
          </p>
        </div>

        {/* Cards de Estatísticas */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 rounded-full bg-blue-100 text-blue-600">
                <FiCalendar size={20} />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Total de Medicamentos</p>
                <p className="text-2xl font-semibold text-gray-900">{estatisticas.totalMedicamentos}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 rounded-full bg-green-100 text-green-600">
                <FiCheckCircle size={20} />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Medicamentos Ativos</p>
                <p className="text-2xl font-semibold text-gray-900">{estatisticas.medicamentosAtivos}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 rounded-full bg-yellow-100 text-yellow-600">
                <FiClock size={20} />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Lembretes Pendentes</p>
                <p className="text-2xl font-semibold text-gray-900">{estatisticas.lembretesPendentes}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Lembretes do Dia */}
        <div className="bg-white rounded-lg shadow">
          <div className="p-6">
            <LembretesDoDia />
          </div>
        </div>
      </div>
    </PrivateLayout>
  );
} 
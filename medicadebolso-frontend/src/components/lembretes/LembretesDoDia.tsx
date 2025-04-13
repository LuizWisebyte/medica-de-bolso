import { useEffect, useState } from 'react';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { FiCheck, FiClock, FiX } from 'react-icons/fi';
import { Lembrete, lembreteService } from '../../services/lembreteService';

export default function LembretesDoDia() {
  const [lembretes, setLembretes] = useState<Lembrete[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  const carregarLembretes = async () => {
    try {
      const hoje = format(new Date(), 'yyyy-MM-dd');
      const data = await lembreteService.listarPorData(hoje);
      setLembretes(data);
    } catch (err) {
      setError('Erro ao carregar lembretes');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    carregarLembretes();
  }, []);

  const handleMarcarComoTomado = async (lembreteId: string) => {
    try {
      await lembreteService.marcarComoTomado(lembreteId);
      await carregarLembretes();
    } catch (err) {
      console.error('Erro ao marcar medicamento como tomado:', err);
    }
  };

  if (isLoading) {
    return (
      <div className="text-center py-4">
        <p>Carregando lembretes...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border-l-4 border-red-400 p-4">
        <div className="flex">
          <div className="flex-shrink-0">
            <FiX className="h-5 w-5 text-red-400" />
          </div>
          <div className="ml-3">
            <p className="text-sm text-red-700">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  const lembretesAgrupados = lembretes.reduce((acc, lembrete) => {
    const horario = lembrete.horario;
    if (!acc[horario]) {
      acc[horario] = [];
    }
    acc[horario].push(lembrete);
    return acc;
  }, {} as Record<string, Lembrete[]>);

  return (
    <div className="space-y-4">
      <h2 className="text-lg font-semibold text-gray-900">
        Lembretes de Hoje - {format(new Date(), "dd 'de' MMMM", { locale: ptBR })}
      </h2>

      {Object.entries(lembretesAgrupados).length === 0 ? (
        <p className="text-gray-500">Nenhum lembrete para hoje.</p>
      ) : (
        Object.entries(lembretesAgrupados)
          .sort(([a], [b]) => a.localeCompare(b))
          .map(([horario, lembretes]) => (
            <div key={horario} className="bg-white rounded-lg shadow p-4">
              <div className="flex items-center mb-2">
                <FiClock className="text-blue-500 mr-2" />
                <h3 className="font-medium text-gray-900">{horario}</h3>
              </div>
              <div className="space-y-3">
                {lembretes.map((lembrete) => (
                  <div
                    key={lembrete.id}
                    className={`flex items-center justify-between p-2 rounded ${
                      lembrete.status === 'TOMADO'
                        ? 'bg-green-50'
                        : lembrete.status === 'ATRASADO'
                        ? 'bg-red-50'
                        : 'bg-gray-50'
                    }`}
                  >
                    <div>
                      <p className="font-medium text-gray-900">
                        {lembrete.medicamentoId}
                      </p>
                      <p className="text-sm text-gray-500">
                        Status: {lembrete.status}
                      </p>
                    </div>
                    {lembrete.status === 'PENDENTE' && (
                      <button
                        onClick={() => handleMarcarComoTomado(lembrete.id)}
                        className="flex items-center px-3 py-1 bg-green-100 text-green-700 rounded-full hover:bg-green-200"
                      >
                        <FiCheck className="mr-1" />
                        Marcar como tomado
                      </button>
                    )}
                  </div>
                ))}
              </div>
            </div>
          ))
      )}
    </div>
  );
} 
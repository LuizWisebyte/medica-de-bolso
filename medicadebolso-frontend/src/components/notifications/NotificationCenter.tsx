import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { FiBell, FiX } from 'react-icons/fi';
import { useAuthStore } from '../../store/authStore';
import { mensagemService } from '../../services/api';

interface Mensagem {
  id: string;
  titulo: string;
  conteudo: string;
  dataEnvio: string;
  lida: boolean;
}

const NotificationCenter = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [mensagens, setMensagens] = useState<Mensagem[]>([]);
  const [contagemNaoLidas, setContagemNaoLidas] = useState(0);
  const { user } = useAuthStore();

  useEffect(() => {
    if (user) {
      const carregarMensagens = async () => {
        const [mensagensData, contagemData] = await Promise.all([
          mensagemService.listarNaoLidas(user.id),
          mensagemService.contarNaoLidas(user.id),
        ]);
        setMensagens(mensagensData);
        setContagemNaoLidas(contagemData);
      };

      carregarMensagens();
      const interval = setInterval(carregarMensagens, 30000); // Atualiza a cada 30 segundos
      return () => clearInterval(interval);
    }
  }, [user]);

  const marcarComoLida = async (mensagemId: string) => {
    await mensagemService.marcarComoLida(mensagemId);
    setMensagens(mensagens.filter((m) => m.id !== mensagemId));
    setContagemNaoLidas((prev) => prev - 1);
  };

  return (
    <div className="relative">
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="p-1 rounded-full text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 relative"
      >
        <FiBell className="h-6 w-6" />
        {contagemNaoLidas > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
            {contagemNaoLidas}
          </span>
        )}
      </button>

      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg ring-1 ring-black ring-opacity-5 z-50"
          >
            <div className="p-4">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-medium text-gray-900">Notificações</h3>
                <button
                  onClick={() => setIsOpen(false)}
                  className="text-gray-400 hover:text-gray-500"
                >
                  <FiX className="h-5 w-5" />
                </button>
              </div>
              <div className="max-h-96 overflow-y-auto">
                {mensagens.length === 0 ? (
                  <p className="text-gray-500 text-center py-4">Nenhuma notificação</p>
                ) : (
                  <ul className="divide-y divide-gray-200">
                    {mensagens.map((mensagem) => (
                      <motion.li
                        key={mensagem.id}
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        className="py-4"
                      >
                        <div className="flex items-start">
                          <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium text-gray-900">
                              {mensagem.titulo}
                            </p>
                            <p className="mt-1 text-sm text-gray-500">
                              {mensagem.conteudo}
                            </p>
                            <p className="mt-1 text-xs text-gray-400">
                              {new Date(mensagem.dataEnvio).toLocaleString()}
                            </p>
                          </div>
                          <button
                            onClick={() => marcarComoLida(mensagem.id)}
                            className="ml-4 text-gray-400 hover:text-gray-500"
                          >
                            <FiX className="h-5 w-5" />
                          </button>
                        </div>
                      </motion.li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default NotificationCenter; 
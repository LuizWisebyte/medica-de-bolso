import { ReactNode, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '../../contexts/AuthContext';
import { notificacaoService } from '../../services/notificacaoService';
import Navbar from '../Navbar';
import toast from 'react-hot-toast';

interface PrivateLayoutProps {
  children: ReactNode;
}

export default function PrivateLayout({ children }: PrivateLayoutProps) {
  const { isAuthenticated, user } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    } else {
      const inicializarNotificacoes = async () => {
        try {
          const permissaoAtual = localStorage.getItem('medicadebolso.notification.permission');
          let permissaoConcedida = permissaoAtual === 'granted';

          if (!permissaoAtual || permissaoAtual === 'default') {
            toast('Para receber lembretes, precisamos da sua permissão para notificações.');
            permissaoConcedida = await notificacaoService.requestPermission();

            if (permissaoConcedida) {
              toast.success('Permissão concedida! Lembretes ativados.');
            } else {
              toast.error('Permissão negada. Você não receberá lembretes.');
            }
          }

          if (permissaoConcedida) {
            await notificacaoService.registrarServiceWorker();
            notificacaoService.iniciarVerificacaoLembretes();
          }
        } catch (error) {
          console.error('Erro ao inicializar notificações:', error);
          toast.error('Erro ao configurar notificações.');
        }
      };

      inicializarNotificacoes();

      return () => {
        notificacaoService.pararVerificacaoLembretes();
      };
    }
  }, [isAuthenticated, router]);

  if (!isAuthenticated || !user) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />
      <main className="container mx-auto px-4 py-8">
        {children}
      </main>
    </div>
  );
} 
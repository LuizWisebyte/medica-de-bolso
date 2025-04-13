import { api } from './api';

class NotificacaoService {
  private readonly NOTIFICATION_PERMISSION_KEY = 'medicadebolso.notification.permission';
  private readonly CHECK_INTERVAL = 60000; // 1 minuto
  private checkInterval: NodeJS.Timeout | null = null;

  async requestPermission(): Promise<boolean> {
    if (!('Notification' in window)) {
      console.warn('Este navegador não suporta notificações desktop');
      return false;
    }

    try {
      const permission = await Notification.requestPermission();
      localStorage.setItem(this.NOTIFICATION_PERMISSION_KEY, permission);
      return permission === 'granted';
    } catch (error) {
      console.error('Erro ao solicitar permissão:', error);
      return false;
    }
  }

  async registrarServiceWorker() {
    if ('serviceWorker' in navigator) {
      try {
        const registration = await navigator.serviceWorker.register('/sw.js');
        const subscription = await registration.pushManager.subscribe({
          userVisibleOnly: true,
          applicationServerKey: process.env.NEXT_PUBLIC_VAPID_PUBLIC_KEY
        });

        // Envia a subscription para o backend
        await api.post('/notificacoes/subscribe', subscription);
      } catch (error) {
        console.error('Erro ao registrar service worker:', error);
      }
    }
  }

  async enviarNotificacao(titulo: string, opcoes: NotificationOptions) {
    if (!('Notification' in window)) {
      return;
    }

    if (Notification.permission === 'granted') {
      try {
        // Tenta usar o service worker primeiro
        const registration = await navigator.serviceWorker?.ready;
        if (registration) {
          await registration.showNotification(titulo, opcoes);
        } else {
          // Fallback para notificação local
          new Notification(titulo, opcoes);
        }
      } catch (error) {
        console.error('Erro ao enviar notificação:', error);
      }
    }
  }

  iniciarVerificacaoLembretes() {
    if (this.checkInterval) {
      clearInterval(this.checkInterval);
    }

    const verificarLembretes = async () => {
      try {
        const response = await api.get('/lembretes/proximos');
        const lembretes = response.data;

        for (const lembrete of lembretes) {
          const horarioLembrete = new Date(lembrete.dataLembrete + 'T' + lembrete.horario);
          const agora = new Date();
          const diferenca = horarioLembrete.getTime() - agora.getTime();

          // Se faltam menos de 5 minutos para o horário do medicamento
          if (diferenca > 0 && diferenca <= 300000) {
            this.enviarNotificacao('Hora do Medicamento!', {
              body: `Está na hora de tomar ${lembrete.medicamentoNome}`,
              icon: '/icons/medicine-icon.png',
              badge: '/icons/badge-icon.png',
              tag: `medicamento-${lembrete.id}`,
              requireInteraction: true,
              actions: [
                {
                  action: 'tomar',
                  title: 'Tomar Agora'
                },
                {
                  action: 'adiar',
                  title: 'Adiar 5min'
                }
              ],
              data: {
                lembreteId: lembrete.id,
                url: `/medicamentos/${lembrete.medicamentoId}`
              }
            });
          }
        }
      } catch (error) {
        console.error('Erro ao verificar lembretes:', error);
      }
    };

    // Verifica imediatamente e depois a cada intervalo
    verificarLembretes();
    this.checkInterval = setInterval(verificarLembretes, this.CHECK_INTERVAL);
  }

  pararVerificacaoLembretes() {
    if (this.checkInterval) {
      clearInterval(this.checkInterval);
      this.checkInterval = null;
    }
  }
}

export const notificacaoService = new NotificacaoService(); 
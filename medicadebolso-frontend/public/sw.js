/* eslint-disable no-restricted-globals */
const CACHE_NAME = 'medicadebolso-v1'; // Cache principal para o App Shell
const API_CACHE_NAME = 'medicadebolso-api-v1'; // Cache separado para dados da API

// Arquivos essenciais do App Shell para pré-cache
const urlsToCache = [
  '/', // Página inicial
  // Adicione outros arquivos essenciais se souber os nomes exatos (ex: /manifest.json)
  '/icons/medicine-icon.png',
  '/icons/badge-icon.png'
];

// Instalação: Pré-cache do App Shell
self.addEventListener('install', (event) => {
  console.log('[Service Worker] Install');
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => {
        console.log('[Service Worker] Precaching App Shell');
        return cache.addAll(urlsToCache);
      })
      .catch(error => {
        console.error('[Service Worker] Failed to pre-cache App Shell:', error);
      })
  );
});

// Ativação: Limpeza de caches antigos
self.addEventListener('activate', (event) => {
  console.log('[Service Worker] Activate');
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          // Deleta caches que não sejam o atual ou o cache da API
          if (cacheName !== CACHE_NAME && cacheName !== API_CACHE_NAME) {
            console.log('[Service Worker] Removing old cache:', cacheName);
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
  return self.clients.claim(); // Torna o SW ativo imediatamente
});

// Fetch: Intercepta requisições e aplica estratégias de cache
self.addEventListener('fetch', (event) => {
  const { request } = event;
  const url = new URL(request.url);

  // 1. Estratégia para API (Network First)
  if (url.pathname.startsWith('/api/')) {
    event.respondWith(
      caches.open(API_CACHE_NAME).then(async (cache) => {
        try {
          // Tenta buscar da rede primeiro
          const networkResponse = await fetch(request);
          // Se sucesso, clona, armazena no cache e retorna
          if (networkResponse.ok) {
            console.log(`[Service Worker] Caching API Response for: ${request.url}`);
            cache.put(request, networkResponse.clone());
          }
          return networkResponse;
        } catch (error) {
          // Se rede falhar, tenta buscar do cache
          console.log(`[Service Worker] Network failed for API: ${request.url}. Trying cache.`);
          const cachedResponse = await cache.match(request);
          if (cachedResponse) {
            console.log(`[Service Worker] Serving API from cache: ${request.url}`);
            return cachedResponse;
          }
          // Se não houver cache, retorna um erro (ou uma resposta padrão offline)
          console.error(`[Service Worker] Network and cache failed for API: ${request.url}`);
          return new Response(JSON.stringify({ error: 'Offline e sem cache disponível' }), {
            headers: { 'Content-Type': 'application/json' },
            status: 503
          });
        }
      })
    );
    return; // Importante: finaliza aqui para requisições de API
  }

  // 2. Estratégia para outros recursos (Cache First)
  // Ignora requisições não-GET e requisições do Chrome Extension
  if (request.method !== 'GET' || request.url.startsWith('chrome-extension://')) {
    event.respondWith(fetch(request));
    return;
  }
  
  event.respondWith(
    caches.match(request).then(async (cachedResponse) => {
      // Se encontrou no cache, retorna
      if (cachedResponse) {
        console.log(`[Service Worker] Serving from cache: ${request.url}`);
        return cachedResponse;
      }

      // Se não encontrou, busca na rede
      try {
        const networkResponse = await fetch(request);
        // Armazena no cache principal e retorna
        const cache = await caches.open(CACHE_NAME);
        console.log(`[Service Worker] Caching new resource: ${request.url}`);
        cache.put(request, networkResponse.clone());
        return networkResponse;
      } catch (error) {
        // Se rede falhar (e não tinha no cache)
        console.error(`[Service Worker] Network failed for non-API: ${request.url}`);
        // Poderia retornar uma página offline padrão aqui se tivesse uma
        // return caches.match('/offline.html');
        return new Response('Network error', { status: 408 });
      }
    })
  );
});

// Interceptação de notificações push
self.addEventListener('push', (event) => {
  if (!event.data) return;

  const data = event.data.json();
  const options = {
    ...data.options,
    icon: data.options.icon || '/icons/medicine-icon.png',
    badge: data.options.badge || '/icons/badge-icon.png',
  };

  event.waitUntil(
    self.registration.showNotification(data.title, options)
  );
});

// Manipulação de cliques nas notificações
self.addEventListener('notificationclick', (event) => {
  event.notification.close();

  if (event.action === 'tomar') {
    // Marca o medicamento como tomado
    const lembreteId = event.notification.data.lembreteId;
    event.waitUntil(
      fetch(`/api/lembretes/${lembreteId}/tomado`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json'
        }
      })
    );
  } else if (event.action === 'adiar') {
    // Adia o lembrete em 5 minutos
    const lembreteId = event.notification.data.lembreteId;
    event.waitUntil(
      fetch(`/api/lembretes/${lembreteId}/adiar`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ minutos: 5 })
      })
    );
  } else {
    // Clique na notificação em si
    event.waitUntil(
      clients.matchAll({ type: 'window' }).then((clientList) => {
        const url = event.notification.data.url || '/';
        
        // Se já tiver uma janela aberta, foca nela
        for (const client of clientList) {
          if (client.url === url && 'focus' in client) {
            return client.focus();
          }
        }
        
        // Se não tiver janela aberta, abre uma nova
        if (clients.openWindow) {
          return clients.openWindow(url);
        }
      })
    );
  }
}); 
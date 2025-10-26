// --- Controle de versão do cache ---
const CACHE_NAME = 'jachegou-v2';
const STATIC_ASSETS = [
    '/manifest.webmanifest',
    '/icons/icon-192.png',
    '/icons/icon-512.png'
];

// --- Instala e pré-carrega ícones básicos ---
self.addEventListener('install', e => {
    self.skipWaiting(); // força ativação imediata
    e.waitUntil(
        caches.open(CACHE_NAME).then(cache => cache.addAll(STATIC_ASSETS))
    );
});

// --- Ativa nova versão e remove caches antigos ---
self.addEventListener('activate', e => {
    e.waitUntil(
        caches.keys().then(keys =>
            Promise.all(keys.map(key => {
                if (key !== CACHE_NAME) {
                    console.log('🧹 Limpando cache antigo:', key);
                    return caches.delete(key);
                }
            }))
        ).then(() => clients.claim())
    );
});

// --- Estratégia de busca ---
// Nunca cacheia HTML (para sempre pegar a versão atual)
self.addEventListener('fetch', e => {
    const req = e.request;

    // evita cache para páginas HTML
    if (req.headers.get('accept')?.includes('text/html')) {
        e.respondWith(fetch(req));
        return;
    }

    // tenta cache para ícones e manifest
    e.respondWith(
        caches.match(req).then(res => res || fetch(req))
    );
});

// --- Web Push Notifications ---
self.addEventListener('push', event => {
    const data = event.data ? event.data.json() : { title: 'Já Chegou', body: 'Atualização disponível.' };
    event.waitUntil(
        self.registration.showNotification(data.title, {
            body: data.body,
            icon: '/icons/icon-192.png',
            badge: '/icons/icon-192.png',
            data: data.url ? { url: data.url } : undefined
        })
    );
});

// --- Ao clicar na notificação ---
self.addEventListener('notificationclick', event => {
    event.notification.close();
    const url = (event.notification.data && event.notification.data.url) || '/';
    event.waitUntil(
        clients.matchAll({ type: 'window' }).then(list => {
            for (const client of list) {
                if (client.url === url && 'focus' in client) return client.focus();
            }
            if (clients.openWindow) return clients.openWindow(url);
        })
    );
});

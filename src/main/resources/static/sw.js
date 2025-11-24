const CACHE_NAME = 'jachegou-v3';
const STATIC_ASSETS = [
    '/',
    '/manifest.webmanifest',
    '/icons/icon-192.png',
    '/icons/icon-512.png'
];

self.addEventListener('install', e => {
    console.log('📦 Instalando Service Worker...');
    self.skipWaiting();
    e.waitUntil(
        caches.open(CACHE_NAME).then(cache => cache.addAll(STATIC_ASSETS))
    );
});

self.addEventListener('activate', e => {
    console.log('Ativando nova versão do Service Worker...');
    e.waitUntil(
        caches.keys().then(keys =>
            Promise.all(
                keys.map(key => {
                    if (key !== CACHE_NAME) {
                        console.log('Limpando cache antigo:', key);
                        return caches.delete(key);
                    }
                })
            )
        ).then(() => clients.claim())
    );
});

self.addEventListener('fetch', e => {
    const req = e.request;

    // evita cache para páginas HTML (sempre buscar do servidor)
    if (req.headers.get('accept')?.includes('text/html')) {
        e.respondWith(fetch(req));
        return;
    }

    e.respondWith(
        caches.match(req).then(res => res || fetch(req))
    );
});

self.addEventListener('push', event => {
    console.log('Push recebido:', event.data ? event.data.text() : '(sem dados)');

    let data = { title: 'Já Chegou', body: 'Atualização disponível.' };
    try {
        if (event.data) {
            data = event.data.json();
        }
    } catch (e) {
        console.warn('Erro ao decodificar push JSON:', e);
    }

    const options = {
        body: data.body || '',
        icon: '/icons/icon-192.png',
        badge: '/icons/icon-192.png',
        data: data.url ? { url: data.url } : undefined
    };

    event.waitUntil(
        self.registration.showNotification(data.title || 'Já Chegou', options)
    );
});

self.addEventListener('notificationclick', event => {
    console.log('Notificação clicada');
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

self.addEventListener('sync', event => {
    if (event.tag === 'sync-notifications') {
        console.log('Sincronização de notificações em segundo plano iniciada');
        event.waitUntil(Promise.resolve());
    }
});

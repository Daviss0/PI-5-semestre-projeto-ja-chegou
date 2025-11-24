package com.ja.chegou.ja_chegou.cache;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class OlhoVivoCache {

    private static final long TEMPO_CACHE_MS = 5000; // 5 segundos
    private final Map<Integer, CacheItem> cache = new HashMap<>();

    public synchronized Map<String, Object> get(int linha, CacheProvider provider) {

        long agora = System.currentTimeMillis();
        CacheItem item = cache.get(linha);

        if (item != null && (agora - item.timestamp) < TEMPO_CACHE_MS) {
            return item.data;
        }

        Map<String, Object> novo = provider.getFromAPI();

        cache.put(linha, new CacheItem(novo, agora));
        return novo;
    }

    private static class CacheItem {
        final Map<String, Object> data;
        final long timestamp;

        CacheItem(Map<String, Object> data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
    }

    @FunctionalInterface
    public interface CacheProvider {
        Map<String, Object> getFromAPI();
    }
}
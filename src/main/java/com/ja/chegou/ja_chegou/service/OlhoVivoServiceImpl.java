package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.cache.OlhoVivoCache;
import com.ja.chegou.ja_chegou.entity.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OlhoVivoServiceImpl implements OlhoVivoService {

    private static final String TOKEN = "0de60b630f2f7fe859e53dff9412c450178648b002615f2733b63a3b90440c25";
    private static final String API_BASE = "https://api.olhovivo.sptrans.com.br/v2.1";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OlhoVivoCache cache;

    @Autowired
    private OSRMService osrmService;

    private boolean autenticado = false;

    // ============================
    // Autenticação
    // ============================
    @Override
    public boolean autenticar() {
        try {
            String url = API_BASE + "/Login/Autenticar?token=" + TOKEN;
            Boolean resp = restTemplate.postForObject(url, null, Boolean.class);
            autenticado = Boolean.TRUE.equals(resp);

            if (autenticado) {
                System.out.println("SPTrans autenticado com sucesso!");
            } else {
                System.err.println("Falha na autenticação SPTrans.");
            }
            return autenticado;

        } catch (Exception e) {
            System.err.println("Erro ao autenticar no Olho Vivo: " + e.getMessage());
            return false;
        }
    }

    // ============================
    // Buscar linha SPTrans por código comercial
    // ============================
    public List<Map<String, Object>> buscarLinhaSPTrans(String codigoComercial) {

        if (!autenticado) autenticar();

        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(API_BASE + "/Linha/Buscar")
                    .queryParam("termosBusca", codigoComercial)
                    .toUriString();

            return restTemplate.getForObject(url, List.class);

        } catch (Exception e) {
            System.err.println("Erro ao buscar linha SPTrans: " + e.getMessage());
            return List.of();
        }
    }

    // ============================
    // Buscar veículos por código comercial
    // ============================
    @Override
    public Map<String, Object> buscarVeiculosPorCodigoComercial(String codigoComercial) {

        if (!autenticado) autenticar();

        return cache.get(codigoComercial.hashCode(),
                () -> buscarVeiculosPorCodigoComercialDireto(codigoComercial));
    }

    private Map<String, Object> buscarVeiculosPorCodigoComercialDireto(String codigoComercial) {
        try {
            if (!autenticado) autenticar();

            // 🔍 Buscar linha pelo nome comercial (ex.: "637A-10")
            List<Map<String, Object>> linhas = buscarLinhaSPTrans(codigoComercial);

            if (linhas == null || linhas.isEmpty()) {
                return Map.of("erro", "linha_nao_encontrada");
            }

            int cl = ((Number) linhas.get(0).get("cl")).intValue();

            // 🔥 Buscar veículos pelo código interno da linha
            String url = API_BASE + "/Posicao/Linha?codigoLinha=" + cl;

            return restTemplate.getForObject(url, Map.class);

        } catch (Exception e) {
            return Map.of(
                    "erro", "sptrans_falhou",
                    "mensagem", "Não foi possível obter veículos"
            );
        }
    }


    // ============================
    // Buscar shape/trajeto
    // ============================
    @Override
    public List<Route.Coordenada> buscarShapeDaLinha(String codigoComercial) {

        if (!autenticado) autenticar();

        try {
            List<Map<String, Object>> linhas = buscarLinhaSPTrans(codigoComercial);

            if (linhas == null || linhas.isEmpty()) {
                return List.of();
            }

            int cl = ((Number) linhas.get(0).get("cl")).intValue();

            String urlParadas = API_BASE + "/Parada/Linha?codigoLinha=" + cl;

            List<Map<String, Object>> paradas = restTemplate.getForObject(urlParadas, List.class);

            if (paradas == null || paradas.isEmpty()) {
                return List.of();
            }

            List<Route.Coordenada> pontos = new ArrayList<>();
            for (Map<String, Object> p : paradas) {
                double lat = ((Number) p.get("py")).doubleValue();
                double lng = ((Number) p.get("px")).doubleValue();
                pontos.add(new Route.Coordenada(lat, lng));
            }

            return pontos;

        } catch (Exception e) {
            return List.of();
        }
    }
}

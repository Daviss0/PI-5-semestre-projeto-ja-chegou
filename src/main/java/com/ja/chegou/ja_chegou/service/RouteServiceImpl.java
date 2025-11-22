package com.ja.chegou.ja_chegou.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.integration.SPTransClient;
import com.ja.chegou.ja_chegou.repository.RouteRepository;
import com.ja.chegou.ja_chegou.service.RouteService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository repository;
    private final SPTransClient spTrans;
    private final ObjectMapper mapper = new ObjectMapper();

    public RouteServiceImpl(RouteRepository repository, SPTransClient spTrans) {
        this.repository = repository;
        this.spTrans = spTrans;
    }

    @Override
    public List<Route> findAll() {
        return repository.findAll();
    }

    @Override
    public Route findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rota não encontrada"));
    }

    @Override
    public Route save(Route route) {
        return repository.save(route);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * 🔥 IMPORTAR ROTA DA SPTRANS
     * Agora usa /Posicao (posição geral)
     * e filtra pela linha (cl)
     */
    public Route importarRotaSPTrans(String codigoLinha) {
        try {
            // 1. Buscar posições gerais
            String json = spTrans.buscarPosicaoGeral();
            JsonNode root = mapper.readTree(json);

            JsonNode linhas = root.get("l");
            if (linhas == null || !linhas.isArray()) {
                throw new RuntimeException("Retorno inválido de /Posicao");
            }

            // 2. Encontrar a linha específica
            JsonNode linhaAlvo = null;
            for (JsonNode linha : linhas) {
                if (linha.get("cl").asInt() == Integer.parseInt(codigoLinha)) {
                    linhaAlvo = linha;
                    break;
                }
            }

            if (linhaAlvo == null) {
                throw new RuntimeException("Linha " + codigoLinha + " não encontrada em /Posicao");
            }

            JsonNode vs = linhaAlvo.get("vs");
            if (vs == null || !vs.isArray() || vs.isEmpty()) {
                throw new RuntimeException("Nenhum veículo ativo na linha " + codigoLinha);
            }

            // 3. Criar shape inicial com posições dos ônibus naquele momento
            List<Route.Coordenada> coords = new ArrayList<>();
            for (JsonNode v : vs) {
                coords.add(new Route.Coordenada(
                        v.get("py").asDouble(),
                        v.get("px").asDouble()
                ));
            }

            // 4. Criar a rota no banco
            Route route = new Route();
            route.setCodigoLinhaOlhoVivo(codigoLinha);
            route.setShape(coords);
            route.setDistanceKm(0.0);

            return repository.save(route);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar rota SPTrans: " + e.getMessage());
        }
    }

    /** 🔥 Posição ao vivo simplificada (para o app) */
    public Map<String, Object> livePositionSimple(String codigoLinha) {
        try {
            String json = spTrans.buscarPosicaoGeral();
            JsonNode root = mapper.readTree(json);

            JsonNode linhas = root.get("l");

            for (JsonNode linha : linhas) {
                if (linha.get("cl").asInt() == Integer.parseInt(codigoLinha)) {

                    JsonNode vs = linha.get("vs");

                    if (vs == null || vs.isEmpty()) {
                        return Map.of("error", "Nenhum veículo ativo");
                    }

                    JsonNode bus = vs.get(0);

                    return Map.of(
                            "lat", bus.get("py").asDouble(),
                            "lng", bus.get("px").asDouble(),
                            "busId", bus.get("p").asText()
                    );
                }
            }

            return Map.of("error", "Linha não encontrada no /Posicao");

        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    @Override
    public List<double[]> loadCoordinates(Long routeId) {
        Route r = findById(routeId);
        List<double[]> list = new ArrayList<>();

        if (r.getShape() != null) {
            for (Route.Coordenada c : r.getShape()) {
                list.add(new double[]{c.getLat(), c.getLng()});
            }
        }
        return list;
    }

    @Override
    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lat2 - lat1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
    }

    @Override
    public List<Route> findClosestRoutes(double userLat, double userLon) {
        return repository.findAll();
    }
}

package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Route;
import java.util.List;
import java.util.Map;

public interface OlhoVivoService {

    boolean autenticar();

    // Buscar veículos por código comercial (ex.: "809A")
    Map<String, Object> buscarVeiculosPorCodigoComercial(String codigoComercial);

    // Buscar shape/trajeto por código comercial
    List<Route.Coordenada> buscarShapeDaLinha(String codigoComercial);
}

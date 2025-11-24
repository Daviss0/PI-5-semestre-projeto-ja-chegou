package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.service.OlhoVivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/olhoVivo")
public class OlhoVivoController {

    @Autowired
    private OlhoVivoService olhoVivoService;


    @GetMapping("/veiculos/{codigoLinha}")
    public ResponseEntity<?> getVeiculos(@PathVariable String codigoLinha) {

        try {
            Map<String, Object> dados =
                    olhoVivoService.buscarVeiculosPorCodigoComercial(codigoLinha);

            if (dados == null || dados.isEmpty() || dados.containsKey("erro")) {
                return ResponseEntity.badRequest().body(dados);
            }

            return ResponseEntity.ok(dados);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "sptrans_falhou"));
        }
    }



    @GetMapping("/trajeto/{codigoLinha}")
    public ResponseEntity<?> getTrajeto(@PathVariable String codigoLinha) {

        try {
            List<Route.Coordenada> shape =
                    olhoVivoService.buscarShapeDaLinha(codigoLinha);

            if (shape == null || shape.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "shape_indisponivel"));
            }

            return ResponseEntity.ok(shape);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "sptrans_falhou"));
        }
    }
}

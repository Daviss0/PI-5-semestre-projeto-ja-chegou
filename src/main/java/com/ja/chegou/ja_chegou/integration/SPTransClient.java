package com.ja.chegou.ja_chegou.integration;

import jakarta.annotation.PostConstruct;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SPTransClient {

    @Value("${sptrans.token}")
    private String apiToken;

    private RestTemplate rest;
    private static final String BASE = "https://api.olhovivo.sptrans.com.br/v2.1";

    @PostConstruct
    public void init() {

        // 🔥 CookieStore garante que LOGIN e REQUISIÇÕES seguintes usem a mesma sessão
        BasicCookieStore cookieStore = new BasicCookieStore();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)   // mantém a sessão ativa
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        this.rest = new RestTemplate(requestFactory);
    }

    /** 🔐 Autenticação obrigatória (gera cookie ASP.NET_SessionId) */
    private void authenticate() {
        String url = BASE + "/Login/Autenticar?token=" + apiToken;

        Boolean ok = rest.postForObject(url, null, Boolean.class);

        if (ok == null || !ok) {
            throw new RuntimeException("Falha ao autenticar na API Olho Vivo.");
        }

        System.out.println("SPTrans autenticado com sucesso!");
    }

    /** 🔍 Buscar dados da linha (para obter CL, SL, etc.) */
    public String buscarLinha(String codigo) {
        authenticate();
        return rest.getForObject(BASE + "/Linha/Buscar?termosBusca=" + codigo, String.class);
    }

    /** 🚌 Buscar posição GERAL dos ônibus (método mais seguro e recomendado) */
    public String buscarPosicaoGeral() {
        authenticate();
        return rest.getForObject(BASE + "/Posicao", String.class);
    }
}

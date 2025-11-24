package com.ja.chegou.ja_chegou.DTO;

public class ApiErrorDTO {
    private String erro;
    private String message;

    public ApiErrorDTO(String erro, String message) {
        this.erro = erro;
        this.message = message;
    }

    public String getErro(){return erro;}
    public String getMessage(){return message;}
}

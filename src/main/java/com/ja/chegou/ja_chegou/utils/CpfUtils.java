package com.ja.chegou.ja_chegou.utils;

public class CpfUtils {

    public static boolean isValidCPF(String cpf) {
        if (cpf == null) return false;

        cpf = cpf.replaceAll("\\D", "");

        // Verifica tamanho
        if (cpf.length() != 11) return false;

        // Evita CPFs com todos os dígitos iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            // Cálculo do primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }
            int resto = 11 - (soma % 11);
            char digito1 = (resto == 10 || resto == 11) ? '0' : (char) (resto + '0');

            // Cálculo do segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }
            resto = 11 - (soma % 11);
            char digito2 = (resto == 10 || resto == 11) ? '0' : (char) (resto + '0');

            // Validação final
            return digito1 == cpf.charAt(9) && digito2 == cpf.charAt(10);

        } catch (Exception e) {
            return false;
        }
    }
}

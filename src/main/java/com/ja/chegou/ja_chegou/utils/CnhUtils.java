package com.ja.chegou.ja_chegou.utils;

import java.time.LocalDate;

public class CnhUtils {

    public static boolean isValidCnhFormat(String cnh) {
        return cnh != null && cnh.matches("\\d{11}");
    }

    public static boolean isValidCNH(String cnh) {
        if (cnh == null || !cnh.matches("\\d{11}")) {
            return false;
        }

        int dsc = 0, soma = 0;
        for (int i = 0, j = 9; i < 9; i++, j--) {
            soma += (cnh.charAt(i) - 48) * j;
        }

        int vl1 = soma % 11;
        if (vl1 >= 10) {
            vl1 = 0;
            dsc = 2;
        }

        soma = 0;
        for (int i = 0, j = 1; i < 9; i++, j++) {
            soma += (cnh.charAt(i) - 48) * j;
        }

        int x = soma % 11;
        int vl2 = (x >= 10) ? 0 : x - dsc;

        String calculado = String.valueOf(vl1) + String.valueOf(vl2);
        return cnh.endsWith(calculado);
    }

    public static boolean isValidCnhValidity(LocalDate validade) {
        return validade != null && !validade.isBefore(LocalDate.now());
    }
}

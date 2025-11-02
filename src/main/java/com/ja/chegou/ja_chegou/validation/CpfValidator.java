package com.ja.chegou.ja_chegou.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) return false;

        // Remove máscara (pontos e traços)
        cpf = cpf.replaceAll("\\D", "");

        if (!cpf.matches("\\d{11}")) return false;

        // Elimina CPFs com todos os dígitos iguais
        if (cpf.chars().distinct().count() == 1) return false;

        try {
            int sum = 0, weight = 10;
            for (int i = 0; i < 9; i++) sum += (cpf.charAt(i) - '0') * weight--;
            int firstDigit = 11 - (sum % 11);
            if (firstDigit > 9) firstDigit = 0;

            sum = 0; weight = 11;
            for (int i = 0; i < 10; i++) sum += (cpf.charAt(i) - '0') * weight--;
            int secondDigit = 11 - (sum % 11);
            if (secondDigit > 9) secondDigit = 0;

            return cpf.charAt(9) - '0' == firstDigit && cpf.charAt(10) - '0' == secondDigit;
        } catch (Exception e) {
            return false;
        }
    }
}
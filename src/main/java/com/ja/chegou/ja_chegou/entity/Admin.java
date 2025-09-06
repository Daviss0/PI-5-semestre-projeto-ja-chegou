package com.ja.chegou.ja_chegou.entity;

import com.ja.chegou.ja_chegou.enumerated.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "TB_ADMIN", uniqueConstraints = {@UniqueConstraint(columnNames = "EMAIL")})
public class Admin extends Usuario{

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;

    public String getCpf() {return cpf;}

    public void setCpf(String cpf) {this.cpf = cpf;}
}

package com.ja.chegou.ja_chegou.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "TB_ADMIN", uniqueConstraints = {@UniqueConstraint(columnNames = "EMAIL")})
public class Admin extends Usuario{

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;

    public String getCpf() {return cpf;}

    public void setCpf(String cpf) {this.cpf = cpf;}
}

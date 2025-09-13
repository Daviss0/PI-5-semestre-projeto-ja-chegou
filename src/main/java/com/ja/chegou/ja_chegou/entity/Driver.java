package com.ja.chegou.ja_chegou.entity;

import com.ja.chegou.ja_chegou.enumerated.CnhCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "TB_DRIVER", uniqueConstraints = {@UniqueConstraint(columnNames = "EMAIL")})
public class Driver extends Usuario{

    @NotBlank(message = "A cnh é obrigatória")
    @Size(min = 5, max = 20, message = "A CNH deve ter entre 5 e 20 caractéres")
    @Column(name = "CNH", unique = true, nullable = false, length = 20)
    private String cnh;

    @NotNull(message = "A categoria da CNH é obrigatória")
    @Enumerated(EnumType.STRING)
    private CnhCategory cnhCategory;

    @NotNull(message = "A validade da CNH é obrigatória")
    @Column(name = "cnh_validity", nullable = false)
    private LocalDate cnhValidity;

    //getters & setters
    public String getCnh() {return cnh;}

    public void setCnh(String cnh) {this.cnh = cnh;}

    public CnhCategory getCnhCategory() {return cnhCategory;}

    public void setCnhCategory( CnhCategory cnhCategory) {this.cnhCategory = cnhCategory;}

    public LocalDate getCnhValidity() {return cnhValidity;}

    public void setCnhValidity(LocalDate cnhValidity) {this.cnhValidity = cnhValidity;}

}

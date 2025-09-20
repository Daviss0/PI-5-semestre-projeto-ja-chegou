package com.ja.chegou.ja_chegou.entity;

import com.ja.chegou.ja_chegou.enumerated.Role;
import com.ja.chegou.ja_chegou.enumerated.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "TB_USUARIO")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "E-mail incorreto")
    @NotBlank(message = "O e-mail é obrigatório")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no minimo 6 caracteres")
    private String password;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 4, max = 100, message = "O nome deve ter entre 4 e 100 caracteres")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;

    @Transient
    private String confPassword;

    // getters & setters
    public void setId(Long id) {this.id = id;}

    public Long getId() {return id;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public  String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public Status getStatus() {return status;}

    public void setStatus(Status status) {this.status = status;}

    public Role getRole() {return role;}

    public void setRole(Role role) {this.role = role;}

    public String getCpf() {return cpf;}

    public void setCpf(String cpf) {this.cpf = cpf;}

    public String getConfPassword() {return confPassword;}

    public void setConfPassword(String confPassword) {this.confPassword = confPassword;}
}

package com.ja.chegou.ja_chegou.entity;

import com.ja.chegou.ja_chegou.enumerated.Role;
import com.ja.chegou.ja_chegou.validation.ValidCPF;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ======== Dados pessoais ========

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 80, message = "O nome deve ter entre 3 e 80 caracteres.")
    private String name;

    @ValidCPF
    @Column(unique = true)
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
    private String password;

    @Column(nullable = true)
    private String phone;

    @Past(message = "A data de nascimento deve estar no passado.")
    @Column(nullable = true)
    private LocalDate birthDate;

    // ======== Endereço ========

    @Column(nullable = true)
    private String cep;

    @Column(nullable = true)
    private String logradouro;

    @Column(nullable = true)
    private String number;

    @Column(nullable = true)
    private String complement;

    @Column(nullable = true)
    private String hood;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String state;

    private Double latitude;
    private Double longitude;

    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    private Role typeUser = Role.CLIENT;

    @Column(length = 500)
    private String observations;

    // ======== Controle ========

    private LocalDateTime registerDate = LocalDateTime.now();
    private LocalDateTime lastAcess;

    // ======== Getters e Setters ========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getHood() {
        return hood;
    }

    public void setHood(String hood) {
        this.hood = hood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Role getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(Role typeUser) {
        this.typeUser = typeUser;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public LocalDateTime getLastAcess() {
        return lastAcess;
    }

    public void setLastAcess(LocalDateTime lastAcess) {
        this.lastAcess = lastAcess;
    }
}

package com.ja.chegou.ja_chegou.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AccountForm {
    @NotBlank
    public String name;

    private LocalDate birthDate;

    private String phone;

    private String email;

    private String cep;
    private String logradouro;
    private String hood;
    private String city;
    private String state;

    private String currentPassword;

    private String newPassword;

    private String confirmPassword;

    //getters & setters

    public @NotBlank String getName() {return name;}

    public void setName(@NotBlank String name) {this.name = name;}

    public LocalDate getBirthDate() {return birthDate;}

    public void setBirthDate(LocalDate birthDate) {this.birthDate = birthDate;}

    public String getPhone() {return phone;}

    public void setPhone(String phone) {this.phone = phone;}

    public String getCep() {return cep;}

    public void setCep(String cep) {this.cep = cep;}

    public String getLogradouro() {return logradouro;}

    public void setLogradouro(String logradouro) {this.logradouro = logradouro;}

    public String getHood() {return hood;}

    public void setHood(String hood) {this.hood = hood;}

    public String getCity() {return city;}

    public void setCity(String city) {this.city = city;}

    public String getState() {return state;}

    public void setState(String state) {this.state = state;}

    public String getCurrentPassword() {return currentPassword;}

    public void setCurrentPassword(String currentPassword) {this.currentPassword = currentPassword;}

    public String getNewPassword() {return newPassword;}

    public void setNewPassword(String newPassword) {this.newPassword = newPassword;}

    public String getConfirmPassword() {return confirmPassword;}

    public void setConfirmPassword(String confirmPassword) {this.confirmPassword = confirmPassword;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public static AccountForm fromClient(Client c) {
        AccountForm f = new AccountForm();
        f.setName(c.getName());
        f.setBirthDate(c.getBirthDate());
        f.setPhone(c.getPhone());
        f.setCep(c.getCep());
        f.setLogradouro(c.getLogradouro());
        f.setHood(c.getHood());
        f.setCity(c.getCity());
        f.setState(c.getState());
        return f;
    }
}

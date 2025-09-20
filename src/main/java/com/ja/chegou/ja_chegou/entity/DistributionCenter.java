package com.ja.chegou.ja_chegou.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "TB_DISTRIBUTION_CENTERS")
public class DistributionCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da central é obrigatório")
    @Size(max = 200, message = "O nome da central deve ter no máximo 200 caracteres")
    private String name;

    @NotBlank(message = "O endereço é obrigatório")
    @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres")
    private String address;

    @NotNull(message = "A latitude é obrigatória")
    @DecimalMin(value = "-90.0", message = "A latitude mínima é -90.0")
    @DecimalMax(value = "90.0", message = "A latitude máxima é 90.0")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "A longitude é obrigatória")
    @DecimalMin(value = "-180.0", message = "A longitude mínima é -180.0")
    @DecimalMax(value = "180.0", message = "A longitude máxima é 180.0")
    @Column(nullable = false)
    private Double longitude;


    //getters & setters
    public void setId(Long id) {this.id = id;}

    public Long getId() {return id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }


}

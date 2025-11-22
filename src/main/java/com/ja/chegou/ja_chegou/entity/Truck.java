package com.ja.chegou.ja_chegou.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "TB_TRUCK")
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Placa é obrigatória")
    @Pattern(
            regexp = "^[A-Z]{3}-\\d{4}$|^[A-Z]{3}[0-9][A-Z][0-9]{2}$",
            message = "A placa deve estar no padrão antigo (ABC-1234) ou Mercosul (ABC1D23)"
    )
    @Column(nullable = false, unique = true)
    private String plate;


    @NotBlank(message = "Modelo é obrigatório")
    @Size(max = 50, message = "Modelo deve ter no máximo 50 caracteres")
    @Column(nullable = false)
    private String model;

    @Size(max = 50, message = "Marca deve ter no máximo 50 caracteres")
    private String brand;

    @NotNull(message = "Capacidade de carga é obrigatória")
    @Positive(message = "Capacidade deve ser maior que zero")
    private Double capacity;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 1900, message = "Ano mínimo é obrigatório")
    @Max(value = 2100, message = "Ano máximo é obrigatório")
    @Column(name = "manufactured_year")
    private Integer manufacturedYear;

    @NotNull
    private Boolean status = true;

    @OneToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    //getters & setters
    public void setId(Long id) {this.id = id;}

    public Long getId() {return id;}

    public String getPlate() {return plate;}

    public void setPlate(String plate) {this.plate = plate;}

    public String getModel() {return model;}

    public void setModel(String model) {this.model = model;}

    public String getBrand() {return brand;}

    public void setBrand(String brand) {this.brand = brand;}

    public Double getCapacity() {return capacity;}

    public void setCapacity(Double capacity) {this.capacity = capacity;}

    public Integer getManufacturedYear() { return manufacturedYear; }

    public void setManufacturedYear(Integer manufacturedYear) { this.manufacturedYear = manufacturedYear; }

    public @NotNull Boolean getStatus() {return status;}

    public void setStatus(@NotNull Boolean status) {this.status = status;}

    public Driver getDriver() {return driver;}

    public void setDriver(Driver driver) {this.driver = driver;}

}

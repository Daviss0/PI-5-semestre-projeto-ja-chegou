package com.ja.chegou.ja_chegou.entity;


import jakarta.persistence.*;


@Entity
@Table (name = "TB_ADMIN", uniqueConstraints = {@UniqueConstraint(columnNames = "EMAIL")})
public class Admin extends Usuarios {


}

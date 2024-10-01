package com.decrypto.operacionescrud.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "PAIS", uniqueConstraints = @UniqueConstraint(columnNames = "NOMBRE"))
public class Pais implements Comparable<Pais>{
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NOMBRE", nullable = false, unique = true)
    private PaisAdmitido nombre;

    @Override
    public int compareTo(Pais otroPais) {
        return this.id.compareTo(otroPais.getId());
    }
}

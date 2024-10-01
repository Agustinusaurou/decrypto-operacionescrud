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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Set;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "COMITENTE", uniqueConstraints = @UniqueConstraint(columnNames = {"IDENTIFICACION", "TIPO_IDENTIFICACION"}))
public class Comitente implements Comparable<Comitente>{
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "IDENTIFICACION")
    private String identificacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_IDENTIFICACION")
    private TipoIdentificador tipoIdentificacion;

    @Column(name = "DESCRIPCION")
    private String description;

    @ManyToMany(mappedBy = "comitentes")
    private Set<Mercado> mercados;

    @Override
    public int compareTo(Comitente otroComitente) {
        return this.id.compareTo(otroComitente.getId());
    }
}

package com.decrypto.operacionescrud.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Set;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "MERCADOS")
public class Mercado {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true)
    private String codigo;

    @Column(name = "DESCRIPCION", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "pais_id", nullable = false)
    private Pais pais;

    @ManyToMany
    @JoinTable(
        name = "COMITENTE_MERCADO",
        joinColumns = @JoinColumn(name = "MERCADO_ID"),
        inverseJoinColumns = @JoinColumn(name = "COMITENTE_ID")
    )
    private Set<Comitente> comitentes;
}

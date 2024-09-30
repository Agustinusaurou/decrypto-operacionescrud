package com.decrypto.operacionescrud.controllers.pais;

import com.decrypto.operacionescrud.entities.PaisAdmitido;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;

@Builder
@Getter
@EqualsAndHashCode
public class PaisModel {
    private Long id;
    private String nombre;
}

package com.decrypto.operacionescrud.controllers.pais;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class PaisModel {
    private Long id;
    private String nombre;
}

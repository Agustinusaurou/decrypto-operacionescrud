package com.decrypto.operacionescrud.entities;

import lombok.Getter;

@Getter
public enum TipoIdentificador {
    DNI("dni"),
    CUIT("cuit");

    private final String description;

    TipoIdentificador(String description) {
        this.description = description;
    }
}

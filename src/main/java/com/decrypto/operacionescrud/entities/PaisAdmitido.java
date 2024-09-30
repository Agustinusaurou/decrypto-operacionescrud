package com.decrypto.operacionescrud.entities;

import lombok.Getter;

@Getter
public enum PaisAdmitido {
    ARGENTINA("Argentina"),
    URUGUAY("Uruguay");

    private final String description;

    PaisAdmitido(String description) {
        this.description = description;
    }
}

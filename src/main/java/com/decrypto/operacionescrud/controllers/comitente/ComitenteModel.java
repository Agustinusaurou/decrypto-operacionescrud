package com.decrypto.operacionescrud.controllers.comitente;

import com.decrypto.operacionescrud.entities.TipoIdentificador;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class ComitenteModel {
    private final Long id;
    private final String nombre;
    private final String identificacion;
    private final TipoIdentificador tipoIdentificacion;
    private final String description;
}

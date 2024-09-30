package com.decrypto.operacionescrud.controllers.pais;

import com.decrypto.operacionescrud.entities.PaisAdmitido;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PaisDTO {
    private Long id;
    private PaisAdmitido nombre;
}

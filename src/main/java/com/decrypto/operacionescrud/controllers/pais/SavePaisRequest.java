package com.decrypto.operacionescrud.controllers.pais;

import com.decrypto.operacionescrud.entities.PaisAdmitido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SavePaisRequest {
    private PaisAdmitido nombre;
}

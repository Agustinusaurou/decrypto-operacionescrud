package com.decrypto.operacionescrud.controllers.mercado;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateMercadoRequest {
    private String description;
}

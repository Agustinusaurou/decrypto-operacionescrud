package com.decrypto.operacionescrud.controllers.mercado;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SaveMercadoRequest {
    @NotBlank
    private String codigo;
    @NotBlank
    private String description;
    @NotBlank
    private String pais;
}

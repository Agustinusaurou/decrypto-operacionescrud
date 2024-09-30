package com.decrypto.operacionescrud.controllers.comitente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateComitenteRequest {
    private String description;
}

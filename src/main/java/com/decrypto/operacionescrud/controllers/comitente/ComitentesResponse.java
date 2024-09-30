package com.decrypto.operacionescrud.controllers.comitente;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ComitentesResponse {
    private final List<ComitenteModel> comitentes;
}

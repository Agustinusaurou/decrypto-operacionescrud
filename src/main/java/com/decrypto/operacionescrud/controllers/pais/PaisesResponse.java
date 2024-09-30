package com.decrypto.operacionescrud.controllers.pais;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PaisesResponse {
    List<PaisModel> paises;
}

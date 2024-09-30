package com.decrypto.operacionescrud.controllers.mercado;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MercadosResponse {
    List<MercadoModel> mercados;
}

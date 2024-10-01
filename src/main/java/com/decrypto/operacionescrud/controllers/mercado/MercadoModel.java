package com.decrypto.operacionescrud.controllers.mercado;

import com.decrypto.operacionescrud.controllers.comitente.ComitenteModel;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class MercadoModel {
    private final Long id;
    private final String codigo;
    private final String description;
    private final PaisAdmitido pais;
    private final List<ComitenteModel> comitentes;
}

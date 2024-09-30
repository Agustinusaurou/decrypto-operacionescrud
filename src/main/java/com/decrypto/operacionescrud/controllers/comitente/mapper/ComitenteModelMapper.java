package com.decrypto.operacionescrud.controllers.comitente.mapper;

import com.decrypto.operacionescrud.controllers.comitente.ComitenteModel;
import com.decrypto.operacionescrud.entities.Comitente;
import org.springframework.stereotype.Component;

@Component
public class ComitenteModelMapper {

    public static ComitenteModel createModel(Comitente comitente) {
        return ComitenteModel.builder()
            .id(comitente.getId())
            .nombre(comitente.getNombre())
            .tipoIdentificacion(comitente.getTipoIdentificacion())
            .identificacion(comitente.getIdentificacion())
            .description(comitente.getDescription())
            .build();
    }
}

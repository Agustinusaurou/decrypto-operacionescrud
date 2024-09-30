package com.decrypto.operacionescrud.controllers.pais.mapper;

import com.decrypto.operacionescrud.controllers.pais.PaisModel;
import com.decrypto.operacionescrud.entities.Pais;
import org.springframework.stereotype.Component;

@Component
public class PaisModelMapper {
    public static PaisModel createModel(Pais pais) {
        return PaisModel.builder()
            .id(pais.getId())
            .nombre(pais.getNombre().name())
            .build();
    }
}

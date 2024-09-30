package com.decrypto.operacionescrud.controllers.mercado.mapper;

import com.decrypto.operacionescrud.controllers.comitente.mapper.ComitenteModelMapper;
import com.decrypto.operacionescrud.controllers.mercado.MercadoModel;
import com.decrypto.operacionescrud.entities.Mercado;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MercadoModelMapper {
    public static MercadoModel createModel(Mercado mercado) {
        return MercadoModel.builder()
            .id(mercado.getId())
            .codigo(mercado.getCodigo())
            .description(mercado.getDescription())
            .pais(mercado.getPais().getNombre())
            .comitentes(mercado.getComitentes().stream().map(ComitenteModelMapper::createModel).collect(Collectors.toList()))
            .build();
    }
}

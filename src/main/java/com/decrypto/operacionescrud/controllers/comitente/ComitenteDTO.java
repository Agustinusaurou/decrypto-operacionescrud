package com.decrypto.operacionescrud.controllers.comitente;

import com.decrypto.operacionescrud.controllers.mercado.MercadoDTO;
import com.decrypto.operacionescrud.entities.TipoIdentificador;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
public class ComitenteDTO {
    private Long id;
    private String nombre;
    private String identificacion;
    private TipoIdentificador tipoIdentificacion;
    private String description;
    private Set<Long> mercadosIds;
}

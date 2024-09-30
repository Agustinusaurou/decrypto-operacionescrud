package com.decrypto.operacionescrud.controllers.mercado;

import com.decrypto.operacionescrud.controllers.comitente.ComitenteDTO;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class MercadoDTO {
    private Long id;
    private String codigo;
    private String description;
    private PaisAdmitido pais;
    private List<ComitenteDTO> comitentes;
}

package com.decrypto.operacionescrud.controllers.comitente;

import com.decrypto.operacionescrud.controllers.mercado.MercadoDTO;
import com.decrypto.operacionescrud.entities.TipoIdentificador;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotBlank;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SaveComitenteRequest {
    @NotBlank
    private String nombre;
    @NotBlank
    private String identificacion;
    @NotBlank
    private String tipoIdentificacion;
    @NotBlank
    private String description;
    private Set<Long> mercadosIds;
}

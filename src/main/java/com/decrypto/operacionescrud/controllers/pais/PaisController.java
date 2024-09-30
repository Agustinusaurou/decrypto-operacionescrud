package com.decrypto.operacionescrud.controllers.pais;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.services.PaisService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/paises")
public class PaisController {
    private final PaisService paisService;

    public PaisController(PaisService paisService) {
        this.paisService = paisService;
    }

    @Operation(summary = "Obtiene todos los paises", description = "Retorna una lista de todos los paises registrados en el sistema.")
    @GetMapping
    public ResponseEntity<PaisesResponse> findAll() {
        Either<PaisService.Left, PaisesResponse> eitherPais = paisService.findAll();
        if (eitherPais.isLeft()) {
            return ResponseEntity.status(buildLeft(eitherPais.getLeft())).build();
        }
        return  ResponseEntity.ok(eitherPais.getRight());
    }

    @Operation(summary = "Obtiene un pais", description = "Retorna el pais correspondiente al id recibido.")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PaisModel> findById(@PathVariable(name = "id") Long id) {
        Either<PaisService.Left, PaisModel> eitherComitente = paisService.findById(id);
        if (eitherComitente.isLeft()) {
            return ResponseEntity.status(buildLeft(eitherComitente.getLeft())).build();
        }
        return  ResponseEntity.ok(eitherComitente.getRight());
    }

    @Operation(summary = "Guarda un pais", description = "Guarda en la base de datos un pais en caso de no existir y de ser un pais admitido.")
    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody SavePaisRequest savePaisRequest) {
        PaisDTO paisDTO = PaisDTO
            .builder()
            .nombre(savePaisRequest.getNombre())
            .build();

        Optional<PaisService.Left> result = paisService.save(paisDTO);

        if (result.isPresent()) {
            return ResponseEntity.status(buildLeft(result.get())).build();
        }
        return  ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Borra un pais", description = "Borra al pais correspondiente al id enviado.")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        Optional<PaisService.Left> result = paisService.delete(id);

        if (result.isPresent()) {
            return ResponseEntity.status(buildLeft(result.get())).build();
        }
        return  ResponseEntity.status(HttpStatus.OK).build();
    }

    public HttpStatus buildLeft (PaisService.Left left) {
        switch (left) {
            case PAIS_EXIST:
            case PAIS_NOT_EXIST:
                return HttpStatus.PRECONDITION_FAILED;
            case UNEXPECTED_ERROR:
            default: {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }
}

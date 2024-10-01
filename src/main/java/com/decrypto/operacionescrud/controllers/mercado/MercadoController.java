package com.decrypto.operacionescrud.controllers.mercado;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
import com.decrypto.operacionescrud.services.MercadoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/mercados")
public class MercadoController {
    private final MercadoService mercadoService;

    public MercadoController(MercadoService mercadoService) {
        this.mercadoService = mercadoService;
    }

    @Operation(summary = "Obtiene todos los mercados", description = "Retorna una lista de todos los mercados registrados en el sistema.")
    @GetMapping
    public ResponseEntity<MercadosResponse> findAll() {
        Either<MercadoService.Left, MercadosResponse> eitherComitentes = mercadoService.findAll();
        if (eitherComitentes.isLeft()) {
            return ResponseEntity.status(buildLeft(eitherComitentes.getLeft())).build();
        }
        return ResponseEntity.ok(eitherComitentes.getRight());
    }

    @Operation(summary = "Obtiene un mercado", description = "Retorna el mercado correspondiente al id recibido.")
    @GetMapping(value = "/{id}")
    public ResponseEntity<MercadoModel> findById(@PathVariable(name = "id") Long id) {
        Either<MercadoService.Left, MercadoModel> eitherComitente = mercadoService.findById(id);
        if (eitherComitente.isLeft()) {
            return ResponseEntity.status(buildLeft(eitherComitente.getLeft())).build();
        }
        return ResponseEntity.ok(eitherComitente.getRight());
    }

    @Operation(summary = "Guarda un mercado", description = "Guarda en la base de datos un mercado en caso de no existir.")
    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody SaveMercadoRequest saveComitenteRequest) {
        MercadoDTO mercadoDTO = MercadoDTO
            .builder()
            .codigo(saveComitenteRequest.getCodigo())
            .pais(PaisAdmitido.valueOf(saveComitenteRequest.getPais()))
            .description(saveComitenteRequest.getDescription())
            .build();

        Optional<MercadoService.Left> result = mercadoService.save(mercadoDTO);

        if (result.isPresent()) {
            return ResponseEntity.status(buildLeft(result.get())).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Actualiza un mercado", description = "Actualiza la descripcion del mercado con el id enviado.")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable(name = "id") Long id,
                                       @RequestBody UpdateMercadoRequest updateComitenteRequest) {
        MercadoDTO mercadoDTO = MercadoDTO
            .builder()
            .id(id)
            .description(updateComitenteRequest.getDescription())
            .build();

        Optional<MercadoService.Left> result = mercadoService.update(mercadoDTO);

        if (result.isPresent()) {
            return ResponseEntity.status(buildLeft(result.get())).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Borra un mercado", description = "Borra el mercado correspondiente al id enviado.")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        Optional<MercadoService.Left> result = mercadoService.delete(id);

        if (result.isPresent()) {
            return ResponseEntity.status(buildLeft(result.get())).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public HttpStatus buildLeft(MercadoService.Left left) {
        switch (left) {
            case MERCADO_NOT_EXIST:
            case MERCADO_EXIST:
            case PAIS_NOT_EXIST:
                return HttpStatus.PRECONDITION_FAILED;
            case UNEXPECTED_ERROR:
            default: {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }
}

package com.decrypto.operacionescrud.controllers.comitente;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.entities.TipoIdentificador;
import com.decrypto.operacionescrud.services.ComitenteService;
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
@RequestMapping("/comitentes")
public class ComitenteController {

    private final ComitenteService comitenteService;

    public ComitenteController(ComitenteService comitenteService) {
        this.comitenteService = comitenteService;
    }

    @Operation(summary = "Obtiene todos los comitentes", description = "Retorna una lista de todos los comitentes registrados en el sistema.")
    @GetMapping
    public ResponseEntity<ComitentesResponse> findAll() {
        Either<ComitenteService.Left, ComitentesResponse> eitherComitentes = comitenteService.findAll();
        if (eitherComitentes.isLeft()) {
            return ResponseEntity.status(buildLeft(eitherComitentes.getLeft())).build();
        }
        return ResponseEntity.ok(eitherComitentes.getRight());
    }

    @Operation(summary = "Obtiene un comitente", description = "Retorna el comitente correspondiente al id recibido.")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ComitenteModel> findById(@PathVariable(name = "id") Long id) {
        Either<ComitenteService.Left, ComitenteModel> eitherComitente = comitenteService.findById(id);
        if (eitherComitente.isLeft()) {
            return ResponseEntity.status(buildLeft(eitherComitente.getLeft())).build();
        }
        return ResponseEntity.ok(eitherComitente.getRight());
    }

    @Operation(summary = "Guarda un comitente", description = "Guarda en la base de datos un comitente en caso de no existir.")
    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody SaveComitenteRequest saveComitenteRequest) {
        ComitenteDTO comitenteDTO = ComitenteDTO
            .builder()
            .nombre(saveComitenteRequest.getNombre())
            .identificacion(saveComitenteRequest.getIdentificacion())
            .tipoIdentificacion(TipoIdentificador.valueOf(saveComitenteRequest.getTipoIdentificacion()))
            .description(saveComitenteRequest.getDescription())
            .mercadosIds(saveComitenteRequest.getMercadosIds())
            .build();

        Optional<ComitenteService.Left> result = comitenteService.save(comitenteDTO);

        if (result.isPresent()) {
            return ResponseEntity.status(buildLeft(result.get())).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Actualiza un comitente", description = "Actualiza la descripcion de un comitente.")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable(name = "id") Long id,
                                       @RequestBody UpdateComitenteRequest updateComitenteRequest) {
        ComitenteDTO comitenteDTO = ComitenteDTO
            .builder()
            .id(id)
            .description(updateComitenteRequest.getDescription())
            .build();

        Optional<ComitenteService.Left> result = comitenteService.update(comitenteDTO);

        if (result.isPresent()) {
            return ResponseEntity.status(buildLeft(result.get())).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Borra un comitente", description = "Borra al comitente correspondiente al id enviado.")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        Optional<ComitenteService.Left> result = comitenteService.delete(id);

        if (result.isPresent()) {
            return ResponseEntity.status(buildLeft(result.get())).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Obtiene todos los comitentes en un mercado", description = "Retorna una lista de todos los comitentes registrados en el sistema para un mercado especifico.")
    @GetMapping(value = "/mercado/{codigo}")
    public ResponseEntity<ComitentesResponse> findByMercado(@PathVariable(name = "codigo") String codigo) {
        Either<ComitenteService.Left, ComitentesResponse> eitherComitente = comitenteService.findComitentesByMercado(codigo);
        if (eitherComitente.isLeft()) {
            return ResponseEntity.status(buildLeft(eitherComitente.getLeft())).build();
        }
        return ResponseEntity.ok(eitherComitente.getRight());
    }

    @Operation(summary = "Guarda un comitente en un mercado", description = "Guarda en la base de datos un comitente en un mercado.")
    @PutMapping(value = "/{id}/mercado/{codigo}")
    public ResponseEntity<Void> saveInMercado(@PathVariable(name = "id") Long idComitente,
                                              @PathVariable(name = "codigo") String codeMercado) {
        Optional<ComitenteService.Left> result = comitenteService.saveInMercado(idComitente, codeMercado);

        if (result.isPresent()) {
            return ResponseEntity.status(buildLeft(result.get())).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private HttpStatus buildLeft(ComitenteService.Left left) {
        switch (left) {
            case COMITENTE_NOT_EXIST:
            case COMITENTE_EXIST:
            case MERCADO_NOT_EXIST:
                return HttpStatus.PRECONDITION_FAILED;
            case UNEXPECTED_ERROR:
            default: {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }
}

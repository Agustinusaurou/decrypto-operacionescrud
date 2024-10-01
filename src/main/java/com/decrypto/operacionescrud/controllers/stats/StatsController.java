package com.decrypto.operacionescrud.controllers.stats;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.services.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/stats")
@RestController
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * Se asume que como un comitente puede estar relacionado con mas de un mercado
     * la suma de los porcentajes de stats puede ser mayor a 100, ya que
     * un mismo comitente puede tomarse en cuenta para mas de un mercado.
     */
    @Operation(summary = "Obtiene los stats", description = "Retorna las cifras totalizadoras de distribución de comitentes por país y mercado")
    @GetMapping
    public ResponseEntity<List<CountryStatsResponse>> getStats() {
        Either<StatsService.Left, List<CountryStatsResponse>> comitenteStats = statsService.getComitenteStats();
        if (comitenteStats.isLeft()) {
            return ResponseEntity.status(buildLeft(comitenteStats.getLeft())).build();
        }
        return ResponseEntity.ok(comitenteStats.getRight());
    }

    public HttpStatus buildLeft(StatsService.Left left) {
        switch (left) {
            case MERCADOS_NOT_EXIST:
                return HttpStatus.PRECONDITION_FAILED;
            case UNEXPECTED_ERROR:
            default: {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }
}

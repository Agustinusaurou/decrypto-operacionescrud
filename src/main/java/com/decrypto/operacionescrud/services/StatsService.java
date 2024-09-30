package com.decrypto.operacionescrud.services;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.controllers.stats.CountryStatsResponse;
import com.decrypto.operacionescrud.controllers.stats.MarketStatsResponse;
import com.decrypto.operacionescrud.controllers.stats.mapper.StatsMapper;
import com.decrypto.operacionescrud.entities.Comitente;
import com.decrypto.operacionescrud.entities.Mercado;
import com.decrypto.operacionescrud.entities.Pais;
import com.decrypto.operacionescrud.reposiroties.ComitenteRepository;
import com.decrypto.operacionescrud.reposiroties.MercadoRepository;
import com.decrypto.operacionescrud.reposiroties.PaisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class StatsService {

    private final ComitenteRepository comitenteRepository;
    private final MercadoRepository mercadoRepository;
    private final PaisRepository paisRepository;

    @Autowired
    public StatsService(ComitenteRepository comitenteRepository,
                        MercadoRepository mercadoRepository,
                        PaisRepository paisRepository) {
        this.comitenteRepository = comitenteRepository;
        this.mercadoRepository = mercadoRepository;
        this.paisRepository = paisRepository;
    }


    public Either<Left, List<CountryStatsResponse>> getComitenteStats() {
        List<Comitente> comitentes;
        try {
            comitentes = comitenteRepository.findAll();
        } catch (Exception e) {
            log.error("Unexpected exception trying to find comitentes", e);
            return Either.left(Left.UNEXPECTED_ERROR);
        }

        if (comitentes.isEmpty()) {
            return Either.left(
                Left.COMITENTES_NOT_EXIST
            );
        }

        List<Mercado> mercados;
        try {
            mercados = mercadoRepository.findAll();
        } catch (Exception e) {
            log.error("Unexpected exception trying to find mercados", e);
            return Either.left(Left.UNEXPECTED_ERROR);
        }

        if (mercados.isEmpty()) {
            return Either.left(
                Left.MERCADOS_NOT_EXIST
            );
        }

        List<Pais> paises;
        try {
            paises = paisRepository.findAll();
        } catch (Exception e) {
            log.error("Unexpected exception trying to find paises", e);
            return Either.left(Left.UNEXPECTED_ERROR);
        }

        if (paises.isEmpty()) {
            return Either.left(
                Left.PAISES_NOT_EXIST
            );
        }

        List<CountryStatsResponse> statsResponseList = new ArrayList<>();

        paises.stream().forEach(p ->
            statsResponseList.add(CountryStatsResponse
                .builder()
                .country(p.getNombre().getDescription())
                .build())
        );

        for (CountryStatsResponse countryStats : statsResponseList) {
            HashMap<String, MarketStatsResponse> statsHashMap = new HashMap<>();

            mercados.stream()
                .filter(m -> m.getPais().getNombre().getDescription().equals(countryStats.getCountry()))
                .forEach(m -> {
                    MarketStatsResponse response = StatsMapper.createModel(m, comitentes.size());
                    statsHashMap.put(m.getCodigo(), response);
                });

            countryStats.setMarket(Arrays.asList(statsHashMap));
        }

        return Either.right(statsResponseList);
    }

    public enum Left {
        UNEXPECTED_ERROR,
        COMITENTES_NOT_EXIST,
        MERCADOS_NOT_EXIST,
        PAISES_NOT_EXIST
    }

}

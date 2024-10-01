package com.decrypto.operacionescrud.services;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.controllers.stats.CountryStatsResponse;
import com.decrypto.operacionescrud.controllers.stats.MarketStatsResponse;
import com.decrypto.operacionescrud.controllers.stats.mapper.StatsMapper;
import com.decrypto.operacionescrud.entities.Comitente;
import com.decrypto.operacionescrud.entities.Mercado;
import com.decrypto.operacionescrud.entities.Pais;
import com.decrypto.operacionescrud.reposiroties.MercadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Service
public class StatsService {

    private final MercadoRepository mercadoRepository;

    @Autowired
    public StatsService(MercadoRepository mercadoRepository) {
        this.mercadoRepository = mercadoRepository;
    }

    @Cacheable(value = "stats")
    public Either<Left, List<CountryStatsResponse>> getComitenteStats() {
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

        ConcurrentSkipListSet<Comitente> comitentes = new ConcurrentSkipListSet<>();
        ConcurrentSkipListSet<Pais> paises = new ConcurrentSkipListSet<>();

        for (Mercado mercado : mercados) {
            comitentes.addAll(mercado.getComitentes());
            paises.add(mercado.getPais());
        }

        List<CountryStatsResponse> statsResponseList = new ArrayList<>();

        paises.stream().forEach(p ->
            statsResponseList.add(CountryStatsResponse
                .builder()
                .country(p.getNombre().getDescription())
                .build())
        );

        for (CountryStatsResponse countryStats : statsResponseList) {
            ConcurrentHashMap<String, MarketStatsResponse> statsHashMap = new ConcurrentHashMap<>();

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
        MERCADOS_NOT_EXIST,
    }

}

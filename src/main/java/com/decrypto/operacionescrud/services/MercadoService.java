package com.decrypto.operacionescrud.services;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.controllers.mercado.MercadoDTO;
import com.decrypto.operacionescrud.controllers.mercado.MercadoModel;
import com.decrypto.operacionescrud.controllers.mercado.MercadosResponse;
import com.decrypto.operacionescrud.controllers.mercado.mapper.MercadoModelMapper;
import com.decrypto.operacionescrud.entities.Mercado;
import com.decrypto.operacionescrud.entities.Pais;
import com.decrypto.operacionescrud.reposiroties.MercadoRepository;
import com.decrypto.operacionescrud.reposiroties.PaisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MercadoService {

    private final MercadoRepository mercadoRepository;
    private final PaisRepository paisRepository;

    @Autowired
    public MercadoService(MercadoRepository mercadoRepository,
                          PaisRepository paisRepository) {
        this.mercadoRepository = mercadoRepository;
        this.paisRepository = paisRepository;
    }

    public Either<Left, MercadosResponse> findAll() {
        List<Mercado> mercados;
        try {
            mercados = mercadoRepository.findAll();
        } catch (Exception e) {
            log.error("Unexpected exception trying to find mercados", e);
            return Either.left(Left.UNEXPECTED_ERROR);
        }

        if (mercados.isEmpty()) {
            return Either.right(
                MercadosResponse.builder().mercados(Collections.emptyList()).build()
            );
        }

        List<MercadoModel> mercadosModel = mercados.stream().map(MercadoModelMapper::createModel).collect(Collectors.toList());

        return Either.right(
            MercadosResponse.builder().mercados(mercadosModel).build()
        );
    }

    public Either<Left, MercadoModel> findById(Long id) {
        Either<Left, Mercado> eitherMercado = findMercadosById(id);
        if (eitherMercado.isLeft()) {
            return Either.left(eitherMercado.getLeft());
        }

        MercadoModel mercado = MercadoModelMapper.createModel(eitherMercado.getRight());

        return Either.right(mercado);
    }

    @CacheEvict(value = "stats", allEntries = true)
    public Optional<Left> save(MercadoDTO dto) {
        Optional<Mercado> optionalMercado;
        try {
            optionalMercado = mercadoRepository.findByCodigo(dto.getCodigo());
        } catch (Exception e) {
            log.error("Unexpected exception trying to find mercado with code '{}' ", dto.getCodigo(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }

        if (optionalMercado.isPresent()) {
            log.info("mercado with codigo '{}' already exists", dto.getCodigo());
            return Optional.of(Left.MERCADO_EXIST);
        }

        Optional<Pais> pais;
        try {
            pais = paisRepository.findByNombre(dto.getPais());
        } catch (Exception e) {
            log.error("Unexpected exception trying to find pais with name '{}' ", dto.getCodigo(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }

        if (!pais.isPresent()) {
            log.info("pais with name '{}' is not exists", dto.getPais().name());
            return Optional.of(Left.PAIS_NOT_EXIST);
        }

        Mercado mercado = Mercado.builder()
            .codigo(dto.getCodigo())
            .description(dto.getDescription())
            .pais(pais.get())
            .build();

        try {
            mercadoRepository.save(mercado);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected exception trying to save mercado with code '{}'", dto.getCodigo(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }
    }

    @CacheEvict(value = "stats", allEntries = true)
    public Optional<Left> update(MercadoDTO dto) {
        Either<Left, Mercado> eitherMercado = findMercadosById(dto.getId());
        if (eitherMercado.isLeft()) {
            return Optional.of(eitherMercado.getLeft());
        }

        Mercado mercado = eitherMercado.getRight();

        mercado.setDescription(dto.getDescription());

        try {
            mercadoRepository.save(mercado);
        } catch (Exception e) {
            log.error("Unexpected exception trying to update mercado with id '{}'", dto.getId(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }
        return Optional.empty();
    }

    @CacheEvict(value = "stats", allEntries = true)
    public Optional<Left> delete(Long id) {
        Either<Left, Mercado> eitherMercado = findMercadosById(id);
        if (eitherMercado.isLeft()) {
            return Optional.of(eitherMercado.getLeft());
        }

        Mercado mercado = eitherMercado.getRight();

        try {
            mercadoRepository.delete(mercado);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected exception trying to delete mercado with id '{}'", id, e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }
    }

    private Either<Left, Mercado> findMercadosById(Long id) {
        Optional<Mercado> optionalMercado;
        try {
            optionalMercado = mercadoRepository.findById(id);
        } catch (Exception e) {
            log.error("Unexpected exception trying to find mercado with id '{}'", id, e);
            return Either.left(Left.UNEXPECTED_ERROR);
        }

        if (!optionalMercado.isPresent()) {
            log.info("mercado with id '{}' not exist", id);
            return Either.left(Left.MERCADO_NOT_EXIST);
        }
        return Either.right(optionalMercado.get());
    }

    public enum Left {
        UNEXPECTED_ERROR,
        MERCADO_EXIST,
        MERCADO_NOT_EXIST,
        PAIS_NOT_EXIST
    }
}

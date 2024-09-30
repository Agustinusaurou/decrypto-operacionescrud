package com.decrypto.operacionescrud.services;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.controllers.pais.PaisDTO;
import com.decrypto.operacionescrud.controllers.pais.PaisModel;
import com.decrypto.operacionescrud.controllers.pais.PaisesResponse;
import com.decrypto.operacionescrud.controllers.pais.mapper.PaisModelMapper;
import com.decrypto.operacionescrud.entities.Pais;
import com.decrypto.operacionescrud.reposiroties.PaisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaisService {

    private final PaisRepository paisRepository;

    @Autowired
    public PaisService(PaisRepository paisRepository) {
        this.paisRepository = paisRepository;
    }

    public Either<Left, PaisesResponse> findAll() {
        List<Pais> paises;
        try {
            paises = paisRepository.findAll();
        } catch (Exception e) {
            log.error("Unexpected exception trying to find paises", e);
            return Either.left(Left.UNEXPECTED_ERROR);
        }

        if (paises.isEmpty()) {
            return Either.right(
                PaisesResponse.builder().paises(Collections.emptyList()).build()
            );
        }

        List<PaisModel> paisesModel = paises.stream().map(PaisModelMapper::createModel).collect(Collectors.toList());

        return Either.right(
          PaisesResponse.builder().paises(paisesModel).build()
        );
    }

    public Either<Left, PaisModel> findById(Long id) {
        Optional<Pais> optionalPais;
        try {
            optionalPais = paisRepository.findById(id);
        } catch (Exception e) {
            log.error("Unexpected exception trying to find pais with id '{}'", id, e);
            return Either.left(Left.UNEXPECTED_ERROR);
        }
        if (!optionalPais.isPresent()) {
            log.info("pais with id '{}' not exist", id);
            return Either.left(Left.PAIS_NOT_EXIST);
        }
        PaisModel pais =  PaisModelMapper.createModel(optionalPais.get());

        return Either.right(pais);
    }

    public Optional<Left> save(PaisDTO dto) {
        Optional<Pais> optionalPais;

        try {
            optionalPais = paisRepository.findByNombre(dto.getNombre());
        } catch (Exception e) {
            log.error("Unexpected exception trying to find pais with name '{}' ", dto.getNombre().name(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }

        if (optionalPais.isPresent()) {
            log.info("pais with name '{}' already exists", dto.getNombre().name());
            return Optional.of(Left.PAIS_EXIST);
        }

        Pais pais = Pais.builder()
            .nombre(dto.getNombre())
            .build();
        try {
            paisRepository.save(pais);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected exception trying to save pais with nombre '{}'", dto.getNombre(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }
    }

    public Optional<Left> delete(Long id) {
        Optional<Pais> optionalPais;
        try {
            optionalPais = paisRepository.findById(id);
        } catch (Exception e) {
            log.error("Unexpected exception trying to find pais with id '{}'", id, e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }
        if (!optionalPais.isPresent()) {
            log.info("pais with id '{}' not exist", id);
            return Optional.of(Left.PAIS_NOT_EXIST);
        }
        try {
            paisRepository.delete(optionalPais.get());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected exception trying to delete pais with id '{}'", id, e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }
    }

    public enum Left {
        UNEXPECTED_ERROR,
        PAIS_EXIST,
        PAIS_NOT_EXIST
    }
}

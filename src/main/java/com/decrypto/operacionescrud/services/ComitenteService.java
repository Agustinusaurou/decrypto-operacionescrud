package com.decrypto.operacionescrud.services;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.controllers.comitente.ComitenteDTO;
import com.decrypto.operacionescrud.controllers.comitente.ComitenteModel;
import com.decrypto.operacionescrud.controllers.comitente.ComitentesResponse;
import com.decrypto.operacionescrud.controllers.comitente.mapper.ComitenteModelMapper;
import com.decrypto.operacionescrud.entities.Comitente;
import com.decrypto.operacionescrud.entities.Mercado;
import com.decrypto.operacionescrud.reposiroties.ComitenteRepository;
import com.decrypto.operacionescrud.reposiroties.MercadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ComitenteService {

    private final ComitenteRepository comitenteRepository;
    private final MercadoRepository mercadoRepository;

    @Autowired
    public ComitenteService(ComitenteRepository comitenteRepository,
                            MercadoRepository mercadoRepository) {
        this.comitenteRepository = comitenteRepository;
        this.mercadoRepository = mercadoRepository;
    }

    public Either<Left, ComitentesResponse> findAll() {
        List<Comitente> comitentes;
        try {
            comitentes = comitenteRepository.findAll();
        } catch (Exception e) {
            log.error("Unexpected exception trying to find comitentes", e);
            return Either.left(Left.UNEXPECTED_ERROR);
        }

        if (comitentes.isEmpty()) {
            return Either.right(
                ComitentesResponse.builder().comitentes(Collections.emptyList()).build()
            );
        }

        List<ComitenteModel> comitentesModel = comitentes.stream().map(ComitenteModelMapper::createModel).collect(Collectors.toList());

        return Either.right(
            ComitentesResponse.builder().comitentes(comitentesModel).build()
        );
    }

    @CacheEvict(value = "stats", allEntries = true)
    public Optional<Left> save(ComitenteDTO dto) {
        Optional<Comitente> optionalComitente;
        try {
            optionalComitente = comitenteRepository.findByIdentificacionAndAndTipoIdentificacion(dto.getIdentificacion(), dto.getTipoIdentificacion());
        } catch (Exception e) {
            log.error("Unexpected exception trying to find comitente with identifier type '{}' and identifier '{}'", dto.getTipoIdentificacion(), dto.getIdentificacion(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }

        if (optionalComitente.isPresent()) {
            log.info("comitente with identifier type '{}' and identifier '{}' already exists", dto.getTipoIdentificacion(), dto.getIdentificacion());
            return Optional.of(Left.COMITENTE_EXIST);
        }
        Set<Mercado> mercados;
        try {
            mercados = mercadoRepository.findMercadosByIdIn(dto.getMercadosIds());
        } catch (Exception e) {
            log.error("Unexpected exception trying to find mercados with id in '{}' ", dto.getMercadosIds().toString(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }

        Comitente comitente = Comitente.builder()
            .nombre(dto.getNombre())
            .identificacion(dto.getIdentificacion())
            .tipoIdentificacion(dto.getTipoIdentificacion())
            .description(dto.getDescription())
            .build();

        try {
            comitente = comitenteRepository.save(comitente);
        } catch (Exception e) {
            log.error("Unexpected exception trying to save comitente with identifier type '{}' and identifier '{}'", dto.getTipoIdentificacion(), dto.getIdentificacion(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }

        Comitente finalComitente = comitente;
        mercados.stream().forEach(m -> m.getComitentes().add(finalComitente));
        try {
            mercadoRepository.saveAll(mercados);
        } catch (Exception e) {
            log.error("Unexpected exception trying to save mercados with new comitente with identifier type '{}' and identifier '{}'", dto.getTipoIdentificacion(), dto.getIdentificacion(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }

        return Optional.empty();
    }

    @CacheEvict(value = "stats", allEntries = true)
    public Optional<Left> update(ComitenteDTO dto) {
        Either<Left, Comitente> eitherComitente = findComitenteById(dto.getId());
        if (eitherComitente.isLeft()) {
            return Optional.of(eitherComitente.getLeft());
        }

        Comitente comitente = eitherComitente.getRight();

        comitente.setDescription(dto.getDescription());

        try {
            comitenteRepository.save(comitente);
        } catch (Exception e) {
            log.error("Unexpected exception trying to update comitente with id '{}'", dto.getId(), e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }
        return Optional.empty();
    }

    @CacheEvict(value = "stats", allEntries = true)
    public Optional<Left> delete(Long id) {
        Either<Left, Comitente> eitherComitente = findComitenteById(id);
        if (eitherComitente.isLeft()) {
            return Optional.of(eitherComitente.getLeft());
        }

        Comitente comitente = eitherComitente.getRight();

        try {
            comitenteRepository.delete(comitente);
        } catch (Exception e) {
            log.error("Unexpected exception trying to delete comitente with id '{}'", id, e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }
        return Optional.empty();
    }

    public Either<Left, ComitenteModel> findById(Long id) {
        Either<Left, Comitente> eitherComitente = findComitenteById(id);
        if (eitherComitente.isLeft()) {
            return Either.left(eitherComitente.getLeft());
        }

        Comitente comitente = eitherComitente.getRight();

        ComitenteModel comitenteModel = ComitenteModelMapper.createModel(comitente);

        return Either.right(comitenteModel);
    }


    private Either<Left, Comitente> findComitenteById(Long id) {
        Optional<Comitente> optionalComitente;
        try {
            optionalComitente = comitenteRepository.findById(id);
        } catch (Exception e) {
            log.error("Unexpected exception trying to find comitente with id '{}'", id, e);
            return Either.left(Left.UNEXPECTED_ERROR);
        }

        if (!optionalComitente.isPresent()) {
            log.info("comitente with id '{}' not exist", id);
            return Either.left(Left.COMITENTE_NOT_EXIST);
        }
        return Either.right(optionalComitente.get());
    }

    public Either<Left, ComitentesResponse> findComitentesByMercado(String code) {
        Optional<Mercado> optionalMercado;
        try {
            optionalMercado = mercadoRepository.findByCodigo(code);
        } catch (Exception e) {
            log.error("Unexpected exception trying to find mercado with code '{}' ", code, e);
            return Either.left(Left.UNEXPECTED_ERROR);
        }

        if (!optionalMercado.isPresent()) {
            log.info("mercado with codigo '{}' does not exists", code);
            return Either.left(Left.MERCADO_NOT_EXIST);
        }

        Set<Comitente> comitentes = optionalMercado.get().getComitentes();

        List<ComitenteModel> comitentesModel = comitentes.stream().map(ComitenteModelMapper::createModel).collect(Collectors.toList());

        return Either.right(ComitentesResponse.builder()
            .comitentes(comitentesModel)
            .build());
    }

    @CacheEvict(value = "stats", allEntries = true)
    public Optional<Left> saveInMercado(Long idComitente, String codeMercado) {
        Either<Left, Comitente> eitherComitente = findComitenteById(idComitente);
        if (eitherComitente.isLeft()) {
            return Optional.of(eitherComitente.getLeft());
        }

        Comitente comitente = eitherComitente.getRight();
        Optional<Mercado> optionalMercado;
        try {
            optionalMercado = mercadoRepository.findByCodigo(codeMercado);
        } catch (Exception e) {
            log.error("Unexpected exception trying to find mercado with code '{}' ", codeMercado, e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }

        if (!optionalMercado.isPresent()) {
            log.info("mercado with codigo '{}' does not exists", codeMercado);
            return Optional.of(Left.MERCADO_NOT_EXIST);
        }

        Mercado mercado = optionalMercado.get();

        if (mercado.getComitentes().contains(comitente)) {
            log.info("comitente with id '{}'  already exists in mercado with codigo '{}'", idComitente, codeMercado);
            return Optional.of(Left.COMITENTE_EXIST);
        }

        mercado.getComitentes().add(comitente);
        try {
            mercadoRepository.save(mercado);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected exception trying to save comitente with id '{}' in mercado with code '{}' ", idComitente, codeMercado, e);
            return Optional.of(Left.UNEXPECTED_ERROR);
        }
    }

    public enum Left {
        UNEXPECTED_ERROR,
        COMITENTE_EXIST,
        COMITENTE_NOT_EXIST,
        MERCADO_NOT_EXIST,
    }
}

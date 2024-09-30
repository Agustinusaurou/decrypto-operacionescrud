package com.decrypto.operacionescrud.reposiroties;

import com.decrypto.operacionescrud.entities.Comitente;
import com.decrypto.operacionescrud.entities.TipoIdentificador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComitenteRepository extends JpaRepository<Comitente, Long> {

    Optional<Comitente> findById(Long appointmentId);

    Optional<Comitente> findByIdentificacionAndAndTipoIdentificacion(String identificacion, TipoIdentificador tipoIdentificacion);

}

package com.decrypto.operacionescrud.reposiroties;

import com.decrypto.operacionescrud.entities.Pais;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {
    Optional<Pais> findByNombre(PaisAdmitido nombre);
}

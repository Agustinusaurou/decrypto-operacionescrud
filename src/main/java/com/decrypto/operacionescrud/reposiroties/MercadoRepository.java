package com.decrypto.operacionescrud.reposiroties;

import com.decrypto.operacionescrud.entities.Mercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface MercadoRepository extends JpaRepository<Mercado, Long> {
    Optional<Mercado> findById(Long appointmentId);

    Optional<Mercado> findByCodigo(String codigo);

    Set<Mercado> findMercadosByIdIn(Set<Long> ids);
}

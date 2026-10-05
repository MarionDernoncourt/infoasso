package com.infoasso.api.repository;

import com.infoasso.api.dto.location.LocationReadDto;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.Location;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location,Long> {

    List<Location> findAllByAssociationId(Long associationId);
    Optional<Location> findByNameIgnoreCaseAndAddressIgnoreCaseAndCityIgnoreCase(String name, String address, String city);
    Optional<Location> findByIdAndAssociationId(Long id, Long associationId);
}

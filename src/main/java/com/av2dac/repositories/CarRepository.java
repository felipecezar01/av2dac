package com.av2dac.repositories;

import com.av2dac.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    // Métodos personalizados para Car, se necessário, podem ser adicionados aqui
}

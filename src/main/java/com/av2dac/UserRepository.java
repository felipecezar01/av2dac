package com.av2dac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Consultas personalizadas podem ser adicionadas aqui no futuro
}

package com.av2dac.repositories;

import com.av2dac.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    // Consultas personalizadas podem ser adicionadas aqui no futuro
}

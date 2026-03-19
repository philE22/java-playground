package com.example.javaplayground.transactional.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SomeEntityRepository extends JpaRepository<SomeEntity, Long> {
}

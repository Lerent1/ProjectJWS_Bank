package org.example.projectjws_bank.repository;

import org.example.projectjws_bank.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.role
        LEFT JOIN FETCH u.account
        WHERE u.username = :username
    """)
    Optional<User> findByUsername(@Param("username") String username);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.role
        LEFT JOIN FETCH u.account
        WHERE u.id = :id
    """)
    Optional<User> findByIdWithDetails(@Param("id") Long id);
}

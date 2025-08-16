package com.sillador.strecs.admin.repository;

import com.sillador.strecs.admin.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);

    @Query(value = "SELECT a.* FROM accounts a " +
               "INNER JOIN account_roles ar ON ar.account_id = a.id " +
               "INNER JOIN roles r ON r.id = ar.role_id " +
               "WHERE r.name = ?1", nativeQuery = true)
    List<Account> findAllByRolesName(String roleName);
}

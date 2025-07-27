package com.sillador.strecs.admin.repository;

import com.sillador.strecs.admin.entity.Account;
import com.sillador.strecs.admin.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String admin);

    List<Role> findAllByName(String teacher);
}

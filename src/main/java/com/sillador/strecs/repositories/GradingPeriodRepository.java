package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.GradingPeriod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradingPeriodRepository extends JpaRepository<GradingPeriod, Long>{
    List<GradingPeriod> findAllByActive(boolean b);

    Optional<GradingPeriod> findByCode(int i);
}

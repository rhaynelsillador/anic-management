package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.GradingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradingPeriodRepository extends JpaRepository<GradingPeriod, Long>{
    List<GradingPeriod> findAllByActive(boolean b);
}

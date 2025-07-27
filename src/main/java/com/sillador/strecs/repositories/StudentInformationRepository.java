package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.Room;
import com.sillador.strecs.entity.StudentInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentInformationRepository extends JpaRepository<StudentInformation, Long>, JpaSpecificationExecutor<StudentInformation> {
}

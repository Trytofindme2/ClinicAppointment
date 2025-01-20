package org.example.unit22_project.Repository;

import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Model.DoctorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorInfoRepo extends JpaRepository<DoctorInfo,Long>
{
    Optional<DoctorInfo>findByDoctor_Id(Long id);

}

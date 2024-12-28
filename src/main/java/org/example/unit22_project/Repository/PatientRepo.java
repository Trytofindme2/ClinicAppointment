package org.example.unit22_project.Repository;

import org.example.unit22_project.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepo extends JpaRepository<Patient,Long>
{
    Optional<Patient>findPatientById(Long id);

    Optional<Patient>findPatientByEmail(String email);

   List<Patient>findPatientByVerified(boolean status);

   long count();

   long countByVerified(boolean status);

}

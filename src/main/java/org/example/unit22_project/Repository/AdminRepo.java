package org.example.unit22_project.Repository;

import org.example.unit22_project.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepo extends JpaRepository<Admin,Long>
{
    Optional<Admin>findAdminByEmailAndPassword(String email,String password);

    Optional<Admin> findAdminByEmail(String email);

    Optional<Admin> findAdminById(Long id);


}

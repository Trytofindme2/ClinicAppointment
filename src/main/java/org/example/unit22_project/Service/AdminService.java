package org.example.unit22_project.Service;

import org.example.unit22_project.Model.Admin;
import org.example.unit22_project.Repository.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService
{
    private final AdminRepo adminRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder() ;

    @Autowired
    public AdminService(AdminRepo adminRepo){
        this.adminRepo = adminRepo;
    }

    //save the admin information
    public boolean EncryptAndSaveAdmInfo(Admin admin) {
        if (!checkPasswordAndReEnterPassword(admin)) {
            return false;
        }
        if (findExitedEmail(admin.getEmail())) {
            return false;
        }
        String adminDBPassword = admin.getPassword();
        String adminEncodedPassword = passwordEncoder.encode(adminDBPassword);
        admin.setPassword(adminEncodedPassword);
        admin.setReEnterPassword("Encoded");
        adminRepo.save(admin);
        System.out.println("Admin saved successfully!");
        return true;
    }

    //find admin by email
    public Admin findAdmByEmail(String email) {
        return adminRepo.findAdminByEmail(email).orElse(null);
    }

    //check the email is exited
    public boolean findExitedEmail(String email){
       Optional<Admin> adminOptional = adminRepo.findAdminByEmail(email);
       if(adminOptional.isPresent()){
           Admin admin = adminOptional.get();
           return true;
       }
       return false;
    }

    //check admin password and re-enterPassword
    public boolean checkPasswordAndReEnterPassword(Admin admin){
        return admin.getPassword().equals(admin.getReEnterPassword());
    }

    //sign in process for admin
    public String SignInProcessForAdmin(String email, String password) {
        Optional<Admin> adminOptional = adminRepo.findAdminByEmail(email);

        if (adminOptional.isEmpty()) {
            return "Account not found with the provided email.";
        }

        Admin admin = adminOptional.get();

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            return "Incorrect password.";
        }

        return "Login successful";
    }

    //find admin by id
    public Admin findAdminById(Long id){
        Optional<Admin> adminOptional = adminRepo.findAdminById(id);
        if (adminOptional.isPresent()){
            return adminOptional.get();
        }
        return null;
    }


}

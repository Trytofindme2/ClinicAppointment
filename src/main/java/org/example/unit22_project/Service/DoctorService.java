package org.example.unit22_project.Service;

import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Model.DutyDate;
import org.example.unit22_project.Repository.DoctorRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService
{
    private final DoctorRepo doctorRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DoctorService(DoctorRepo doctorRepo){
        this.doctorRepo = doctorRepo;
    }

    //check the password match
    public boolean passwordsMatch(Doctor doctor) {
        return doctor.getPassword().equals(doctor.getReEnterPassword());
    }

    //check the email exist
    public boolean isEmailAvailable(String email) {
        return doctorRepo.findDoctorByEmail(email).isEmpty();
    }

    //encode and save doctor information
    public boolean encodeAndSaveDoctorInfo(Doctor doctor) {
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        doctor.setReEnterPassword("Encoded");
        doctorRepo.save(doctor);
        return true;
    }

    //find doctor with email
    public Doctor findDoctorWithEmail(String email){
        return doctorRepo.findDoctorByEmail(email).get();
    }

    //find doctor by email
    public boolean findDoctorByEmail(String email){
        return doctorRepo.findDoctorByEmail(email).isPresent();
    }

    //doctor sign in process
    public String signInProcess(String email,String password)
    {
        Optional<Doctor> doctorOptional = doctorRepo.findDoctorByEmail(email);
        if(doctorOptional.isEmpty()){
            return "Account not found with the provided email.";
        }
        Doctor doctor = doctorOptional.get();
        if(!passwordEncoder.matches(password,doctor.getPassword())){
            return "Incorrect password.";
        }
        if(!doctor.isVerified()){
            return "Your account is not verified.";
        }
        return null;
    }

     //get doctor list for doctor by verified = false
    public List<Doctor> findDoctorByUnverified(){
        return doctorRepo.findDoctorByVerified(false);
    }

    //get doctor list for doctor by verified = true
    public List<Doctor> findDoctorByVerified(){
        return doctorRepo.findDoctorByVerified(true);
    }

    //change verified status for doctor by admin
    public void changeVerifiedStatus(Long id){
        Optional<Doctor> doctorOptional = doctorRepo.findDoctorById(id);
        if(doctorOptional.isPresent()){
            Doctor doctor = doctorOptional.get();
            doctor.setVerified(true);
            doctorRepo.save(doctor);
        }
    }

    //delete doctor by id
    public void deleteDoctorById(Long id){
        Optional<Doctor> doctorOptional = doctorRepo.findDoctorById(id);
        if(doctorOptional.isPresent()){
            Doctor doctor= doctorOptional.get();
            doctorRepo.deleteById(id);
        }
    }

    //find doctor by id
    public Doctor findDoctorById(Long id){
        Optional<Doctor> doctorOptional = doctorRepo.findDoctorById(id);
        if(doctorOptional.isPresent()){
            Doctor doctor = doctorOptional.get();
            return doctor;
        }
        return null;
    }

    //change password for the doctor
    public String changePassword(Long id,String oldPassword , String newPassword, String newReEnterPassword){
        Optional<Doctor> doctorOptional = doctorRepo.findDoctorById(id);
        if(doctorOptional.isEmpty()){
            return "User not found. Please log in first.";
        }
        Doctor doctor = doctorOptional.get();
        if(!passwordEncoder.matches(oldPassword,doctor.getPassword())){
           return "Old password is incorrect.";
        }
        if(!newPassword.equals(newReEnterPassword)){
            return "New passwords do not match.";
        }
        doctor.setPassword(passwordEncoder.encode(newPassword));
        doctor.setReEnterPassword("Encoded");
        doctorRepo.save(doctor);
        return "Reset Password is Done";
    }

    //get doctor count
    public long getDoctorCount() {
        return doctorRepo.count();
    }


    //get doctor count by verified status
    public long getDoctorCountByStatus(){
        return doctorRepo.countByVerified(false);
    }

    public List<Doctor> findDoctorBySpecialization(String specialization) {
        // Assume you have a repository to query the doctors by specialization
        if ("all".equalsIgnoreCase(specialization)) {
            return doctorRepo.findAll();
        } else {
            return doctorRepo.findDoctorsWithInfoBySpecialization(specialization); // Ensure this method filters by specialization
        }
    }
    public List<Doctor> getAllDoctorList(){
        return doctorRepo.findAll();
    }

    public List<Doctor> searchDoctors(String query) {
        return doctorRepo.searchDoctorsByNameOrSpecialization(query);
    }

    public List<Doctor> getDoctorsByDutyDate(LocalDate date)
    {
        List<Doctor> doctors = doctorRepo.findDoctorByDutyDates(LocalDate.of(2024, 12, 23));
        System.out.println(doctors);
        return doctorRepo.findDoctorByDutyDates(date);
    }


}

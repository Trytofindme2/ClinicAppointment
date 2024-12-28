package org.example.unit22_project.Service;

import org.example.unit22_project.Model.Patient;
import org.example.unit22_project.Repository.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    private final PatientRepo patientRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public PatientService(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    //check the password
    public boolean checkPasswordAndReEnterPassword(Patient patient) {
        if (patient.getPassword().equals(patient.getReEnterPassword())) {
            return true;
        }
        return false;
    }

    //find the exited user
    public boolean findExitedUser(String email) {
        Patient patient = findPatientByEmail(email);
        if (patient != null) {
            return true;
        } 
        return false;
    }

    //find the user by email
    public Patient findPatientByEmail(String email) {
        Optional<Patient> patientOptional = patientRepo.findPatientByEmail(email);
        if (patientOptional.isPresent()) {
            Patient patient = patientOptional.get();
            return patient;
        }
        return null;
    }

    //save the user info
    public boolean EncryptAndSaveInfo(Patient patient) {
        if (checkPasswordAndReEnterPassword(patient) && findExitedUser(patient.getEmail()) == false) {
            patient.setPassword(passwordEncoder.encode(patient.getPassword()));
            patient.setReEnterPassword("Encoded");
            patientRepo.save(patient);
            return true;
        }
        return false;
    }

    //find user by verified status -> false
    public List<Patient> findUserByUnVerifiedStatus() {
        return patientRepo.findPatientByVerified(false);
    }


    //check user info for log in process
    public String UserLogInProcess(String email, String password) {
        Optional<Patient> patientOptional = patientRepo.findPatientByEmail(email);

        if (patientOptional.isEmpty()) {
            return "Account not found with the provided email.";
        }
        Patient patient = patientOptional.get();
        if (!passwordEncoder.matches(password, patient.getPassword())) {
            return "Incorrect password.";
        }
        if (!patient.isVerified()) {
            return "Your account is not verified.";
        }
        return  null;
    }

    //get the number of users
    public long getUserCount() {
        return patientRepo.count();
    }


    //get the user number who are not verified
    public long getUnverifiedUserCount() {
        return patientRepo.countByVerified(false);
    }

    public void changeVerifiedStatus(Long id) {
        Optional<Patient> optionalPatient = patientRepo.findPatientById(id);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            patient.setVerified(true);
            patientRepo.save(patient);
        }
    }

    public void rejectRequest(Long id) {
        Optional<Patient> optionalPatient = patientRepo.findPatientById(id);
        if (optionalPatient.isPresent())
        {
            Patient patient = optionalPatient.get();
            patientRepo.deleteById(id);
        }
    }

}
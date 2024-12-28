package org.example.unit22_project.Service;

import jakarta.transaction.Transactional;
import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Model.DoctorInfo;
import org.example.unit22_project.Repository.DoctorInfoRepo;
import org.example.unit22_project.Repository.DoctorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class DoctorInfoService
{
    private final DoctorInfoRepo doctorInfoRepo;

    private final DoctorRepo doctorRepo;

    @Autowired
    public DoctorInfoService(DoctorInfoRepo doctorInfoRepo,
                             DoctorRepo doctorRepo){
        this.doctorInfoRepo=doctorInfoRepo;
        this.doctorRepo = doctorRepo;
    }

    public void saveDoctorInfo(DoctorInfo doctorInfo){
        doctorInfoRepo.save(doctorInfo);
    }

    public boolean checkDoctorInfo(Long id){
        Optional<DoctorInfo> doctorInfoOptional = doctorInfoRepo.findByDoctor_Id(id);
        if(doctorInfoOptional.isPresent()){
            return true ;
        }
        else {
            return false;
        }
    }

    public void updateDoctorInfo(DoctorInfo updatedDoctorInfo, Long doctorId) {
        Optional<DoctorInfo> existingInfoOptional = doctorInfoRepo.findByDoctor_Id(doctorId);
        if (existingInfoOptional.isPresent()) {
            DoctorInfo existingInfo = existingInfoOptional.get();
            existingInfo.setProfilePhoto(updatedDoctorInfo.getProfilePhoto() != null ? updatedDoctorInfo.getProfilePhoto() : existingInfo.getProfilePhoto());
            existingInfo.setFeesRate(updatedDoctorInfo.getFeesRate());
            existingInfo.setPhNumber(updatedDoctorInfo.getPhNumber());
            existingInfo.setAddress(updatedDoctorInfo.getAddress());
            existingInfo.setSpecialization(updatedDoctorInfo.getSpecialization());
            existingInfo.setExperienceYears(updatedDoctorInfo.getExperienceYears());
            existingInfo.setQualifications(updatedDoctorInfo.getQualifications());
            doctorInfoRepo.save(existingInfo);
        } else {
            throw new IllegalArgumentException("Doctor information not found for the provided ID.");
        }
    }

    public Optional<DoctorInfo> findDoctorInfoByDoctorId(Long id)
    {
        return doctorInfoRepo.findByDoctor_Id(id);
    }


    //update doctor info and email
    public String updateDoctorInfo(Long id,String email,String phNumber, String address, double fees){
        if(id != null)
        {
            Doctor doctor = doctorRepo.findDoctorById(id).get();
            doctor.setEmail(email);
            doctorRepo.save(doctor);
            DoctorInfo doctorInfo = doctorInfoRepo.findByDoctor_Id(id).get();
            doctorInfo.setPhNumber(phNumber);
            doctorInfo.setFeesRate(fees);
            doctorInfo.setAddress(address);
            doctorInfoRepo.save(doctorInfo);
            return "Updated Successfully";
        }
        return "Something wrong";
    }

    @Transactional
    public void deleteDoctorInfoById(Long doctorId){
        doctorRepo.deleteById(doctorId);
    }

}

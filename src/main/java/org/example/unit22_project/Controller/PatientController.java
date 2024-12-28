package org.example.unit22_project.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.unit22_project.Model.*;
import org.example.unit22_project.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/index/user")
public class PatientController
{
    private final PatientService patientService;

    private final DoctorService doctorService;

    private final DoctorInfoService doctorInfoService;

    private final DutyDateService dutyDateService;

    private final DutyTimeService dutyTimeService;


    @Autowired
    public PatientController(PatientService patientService,
                             DoctorService doctorService,
                             DoctorInfoService doctorInfoService,
                             DutyDateService dutyDateService,
                             DutyTimeService dutyTimeService){
        this.patientService = patientService;
        this.doctorInfoService = doctorInfoService;
        this.doctorService = doctorService;
        this.dutyDateService = dutyDateService;
        this.dutyTimeService = dutyTimeService;
    }

    @GetMapping("/SignUp")
    public String getUserSignInPage(Model model){
        model.addAttribute("newUser",new Patient());
        return "UserSignUp";
    }

    @PostMapping("/SignUpProcess")
    public String SignUpUserInfo(@ModelAttribute("newUser")Patient patient,Model model){
        if (patientService.findExitedUser(patient.getEmail())){
            model.addAttribute("errorMessage", "Email already exists !");
            return "UserSignUp";
        }
        if (!patientService.checkPasswordAndReEnterPassword(patient)) {
            model.addAttribute("errorMessage", "Passwords do not match !");
            return "UserSignUp";
        }
        boolean isSaved = patientService.EncryptAndSaveInfo(patient);
        if (!isSaved) {
            model.addAttribute("errorMessage", "Sign-up failed. Please try again !");
            return "UserSignUp";
        }
        model.addAttribute("successMessage", "Account created successfully! Wait For Verified.");
        return "UserSignUp";
    }

    @GetMapping("/SignIn")
    public String getUserSignIn(){
        return "UserSignIn";
    }

    @PostMapping("/SignInProcess")
    public String userSignInProcess(@RequestParam("email")String email,
                                    @RequestParam("password")String password,
                                    Model model,HttpSession httpSession)
    {
        String errorMessage = patientService.UserLogInProcess(email, password);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            return "UserSignIn";
        }
        httpSession.setAttribute("userId",patientService.findPatientByEmail(email).getId());
        return "redirect:/index/user/CityStar";
    }

    @GetMapping("/CityStar")
    public String getMainPage(Model model, HttpSession httpsession)
    {
        Long userId = (Long) httpsession.getAttribute("userId");
        if(userId != null){
            model.addAttribute("isLogin",true);
            System.out.println("User id is"+ userId);
        }
        else {
            model.addAttribute("isLogin",false);
        }
        return "MainPage";
    }

    @GetMapping("/AboutUs")
    public String getAboutUsPage(Model model,HttpSession httpSession){
        Long userId = (Long) httpSession.getAttribute("userId");
        if(userId != null){
            model.addAttribute("isLogin",true);
        }
        else{
            model.addAttribute("isLogin",false);
        }
        return "AboutUs";
    }

    @GetMapping("/SignOut")
    public String userSignOut(HttpSession httpSession)
    {
        httpSession.setAttribute("userId",null);
        return "redirect:/index/user/CityStar";
    }

    @GetMapping("/getDoctorBySpec")
    public String getdoctorbySpec(@RequestParam(value = "spec", defaultValue = "all") String specialization,
                                  HttpSession httpSession, Model model) {
        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLogin", true);
        } else {
            model.addAttribute("isLogin", false);
        }
        List<Doctor> doctorList;
        if ("all".equalsIgnoreCase(specialization)) {
            doctorList = doctorService.getAllDoctorList();
            System.out.println(doctorList.toString());
        } else {
            doctorList = doctorService.findDoctorBySpecialization(specialization);
        }
        model.addAttribute("doctorList", doctorList);
        return "MainPageDoctorList";
    }

    @GetMapping("/searchDoctors")
    @ResponseBody
    public List<Doctor> searchDoctors(@RequestParam("query") String query) {
        return doctorService.searchDoctors(query);
    }


    @GetMapping("/getDoctorByDate")
    @ResponseBody
    public List<Doctor> getDoctorsBySpecAndDate(@RequestParam(value = "dutyDate") LocalDate dutyDate) {
        return doctorService.getDoctorsByDutyDate(dutyDate);
    }

    @GetMapping("/profilePhoto")
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePhoto(@RequestParam("doctorId")Long doctorId)
    {
        DoctorInfo doctorInfo = doctorInfoService.findDoctorInfoByDoctorId(doctorId).get();
        byte[] image = doctorInfo.getProfilePhoto();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @GetMapping("/explore")
    public String exploreDoctorPage(HttpSession httpSession,
                                    @RequestParam("doctorId")Long doctorId,
                                    Model model){
        Long userId = (Long) httpSession.getAttribute("userId");
        if(userId != null){
            model.addAttribute("isLogin",true);
        }
        else {
            model.addAttribute("isLogin",false);
        }
        Doctor doctor = doctorService.findDoctorById(doctorId);
        model.addAttribute("doctorInfo",doctor);
        List<DutyDate> dutyDates = dutyDateService.showAllDutyDateByDoctorId(doctorId);
        model.addAttribute("Dates",dutyDates);
        return "MainPageDoctorProfile";
    }






}

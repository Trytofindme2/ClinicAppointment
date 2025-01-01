package org.example.unit22_project.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.unit22_project.Model.*;
import org.example.unit22_project.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/index/user")
public class PatientController {
    private final PatientService patientService;

    private final DoctorService doctorService;

    private final DoctorInfoService doctorInfoService;

    private final DutyDateService dutyDateService;

    private final DutyTimeService dutyTimeService;

    private final AppointmentService appointmentService;


    @Autowired
    public PatientController(PatientService patientService,
                             DoctorService doctorService,
                             DoctorInfoService doctorInfoService,
                             DutyDateService dutyDateService,
                             DutyTimeService dutyTimeService,
                             AppointmentService appointmentService) {
        this.patientService = patientService;
        this.doctorInfoService = doctorInfoService;
        this.doctorService = doctorService;
        this.dutyDateService = dutyDateService;
        this.dutyTimeService = dutyTimeService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/SignUp")
    public String getUserSignInPage(Model model) {
        model.addAttribute("newUser", new Patient());
        return "UserSignUp";
    }

    @PostMapping("/SignUpProcess")
    public String SignUpUserInfo(@ModelAttribute("newUser") Patient patient, Model model) {
        if (patientService.findExitedUser(patient.getEmail())) {
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
    public String getUserSignIn() {
        return "UserSignIn";
    }

    @PostMapping("/SignInProcess")
    public String userSignInProcess(@RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    Model model, HttpSession httpSession) {
        String errorMessage = patientService.UserLogInProcess(email, password);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            return "UserSignIn";
        }
        httpSession.setAttribute("userId", patientService.findPatientByEmail(email).getId());
        return "redirect:/index/user/CityStar";
    }

    @GetMapping("/CityStar")
    public String getMainPage(Model model, HttpSession httpsession) {
        Long userId = (Long) httpsession.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLogin", true);
            System.out.println("User id is" + userId);
        } else {
            model.addAttribute("isLogin", false);
        }
        return "MainPage";
    }

    @GetMapping("/AboutUs")
    public String getAboutUsPage(Model model, HttpSession httpSession) {
        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLogin", true);
        } else {
            model.addAttribute("isLogin", false);
        }
        return "AboutUs";
    }

    @GetMapping("/SignOut")
    public String userSignOut(HttpSession httpSession) {
        httpSession.setAttribute("userId", null);
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
    public ResponseEntity<byte[]> getProfilePhoto(@RequestParam("doctorId") Long doctorId) {
        DoctorInfo doctorInfo = doctorInfoService.findDoctorInfoByDoctorId(doctorId).get();
        byte[] image = doctorInfo.getProfilePhoto();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @GetMapping("/getDutyTimes")
    @ResponseBody
    public List<DutyTimeDTO> getDutyTimesByDate(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("dutyDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dutyDate) {
        return dutyTimeService.getDutyTimes(dutyDate, doctorId);
    }

    @GetMapping("/explore")
    public String exploreDoctorPage(HttpSession httpSession,
                                    @RequestParam("doctorId") Long doctorId,
                                    Model model) {
        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLogin", true);
            model.addAttribute("userId",userId);
        } else {
            model.addAttribute("isLogin", false);
        }
        Doctor doctor = doctorService.findDoctorById(doctorId);
        model.addAttribute("doctorInfo", doctor);
        List<DutyDate> dutyDates = dutyDateService.showAllDutyDateByDoctorId(doctorId);
        model.addAttribute("Dates", dutyDates);
        return "MainPageDoctorProfile";
    }

    @GetMapping("/getAllAppointment")
    @ResponseBody
    public List<Appointment>getAllApppointment(){
        return appointmentService.getAllAppointment();
    }


    @PostMapping("/bookedAppointment")
    public String bookedAppointmentProcess(@RequestParam("doctorId") Long doctorId,
                                           @RequestParam("appointmentDate") LocalDate appointmentDate,
                                           @RequestParam("appointmentTime") LocalTime appointmentTime,
                                           HttpSession httpSession) {

        Long userId = (Long) httpSession.getAttribute("userId");
        String returnMessage;

        if (userId != null) {
            boolean isAvailable = appointmentService.isAppointmentSlotAvailable(doctorId, appointmentDate, appointmentTime);
            if (!isAvailable) {
                returnMessage = "The Selected Time Slot is Unavailable for An Appointment. Please Choose Another.";
            } else {
                returnMessage = appointmentService.SaveAppointment(doctorId, userId, appointmentDate, appointmentTime);

            }
        } else {
            returnMessage = "You need to log in to book an appointment.";
        }
        return "redirect:/index/user/explore?doctorId=" + doctorId + "&returnMessage=" + returnMessage;
    }

    @GetMapping("/Ticket")
    public String getHistory(Model model,HttpSession httpSession){
        Long userId = (Long) httpSession.getAttribute("userId");
        if(userId!=null){
            model.addAttribute("isLogin", true);
            List<Appointment> appointmentList = appointmentService.getAppointmentByPatientId(userId);
            if(appointmentList != null){
                model.addAttribute("appointmentHistoryList",appointmentList);
            }
            else {
                model.addAttribute("errorMessage", "No appointment found.");
            }
        } else {
            model.addAttribute("isLogin", false);
            model.addAttribute("errorMessage", "You must log in to view your appointment Ticket.");
        }
        return "MainPageAppointmentTicket";
    }

    @GetMapping("/History")
    public String getAppointmentHistory(HttpSession httpSession,Model model)
    {
        Long userId = (Long) httpSession.getAttribute("userId");
        if(userId != null){
            model.addAttribute("isLogin",true);
            List<AppointmentHistory> appointmentHistoryList = appointmentService.appointmentHistoryList(userId);
            model.addAttribute("appointmentHistoryList",appointmentHistoryList);
        }
        else {
            model.addAttribute("isLogin",false);
            model.addAttribute("errorMessage", "You must log in to view your appointment Ticket.");
        }
        return "MainPageHistory";
    }


    @GetMapping("/viewTicket")
    public String getViewTicket(@RequestParam("appointmentId")Long appointmentId,
                                Model model){
       Appointment appointment = appointmentService.getAppointmentById(appointmentId).get();
       model.addAttribute("appointment",appointment);
       return "ViewTicket";
    }




}








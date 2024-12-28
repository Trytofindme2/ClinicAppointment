package org.example.unit22_project.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.unit22_project.Model.Admin;
import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Model.DoctorInfo;
import org.example.unit22_project.Service.AdminService;
import org.example.unit22_project.Service.DoctorInfoService;
import org.example.unit22_project.Service.DoctorService;
import org.example.unit22_project.Service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/index/admin")
public class AdminController
{
    private final AdminService adminService;

    private final DoctorInfoService doctorInfoService;

    private final DoctorService doctorService;

    private  final PatientService patientService;

    @Autowired
    public AdminController(AdminService adminService,
                           PatientService patientService,
                           DoctorService doctorService,
                           DoctorInfoService doctorInfoService){
        this.adminService = adminService;
        this.patientService = patientService;
        this.doctorService =doctorService;
        this.doctorInfoService = doctorInfoService;
    }

    //get the admin dashboard
    @GetMapping("/DashBoard")
    public String getAdmDashBoard(Model model,
                                  HttpSession httpSession)
    {
        Long adminId = (Long) httpSession.getAttribute("id");
        if (adminId != null) {
            Admin admin = adminService.findAdminById(adminId);
            model.addAttribute("admin", admin);
            model.addAttribute("isLogin", true);
            model.addAttribute("userCount",patientService.getUserCount());
            model.addAttribute("doctorCount",doctorService.getDoctorCount());
            model.addAttribute("unverifiedDoctorCount",doctorService.getDoctorCountByStatus());
            model.addAttribute("unverifiedUserCount",patientService.getUnverifiedUserCount());
        } else {
            model.addAttribute("isLogin", false);
        }
        return "AdminDashBoard";

    }

    //get sign up page
    @GetMapping("/SignUp")
    public String getAdmSignUpForm(Model model)
    {
        model.addAttribute("newAdm",new Admin());
        return "AdminSignUp";
    }

    //sign up process for admin
    @PostMapping("/SignUpProcess")
    public String admSignUpProcess(@ModelAttribute("newAdm") Admin admin, Model model) {
        System.out.println("admSignUpProcess: Received admin data: " + admin);

        if (adminService.findExitedEmail(admin.getEmail())) {
            model.addAttribute("errorMessage", "Email already exists!");
            System.out.println("Error: Email already exists.");
            return "AdminSignUp";
        }
        if (!adminService.checkPasswordAndReEnterPassword(admin)) {
            model.addAttribute("errorMessage", "Passwords do not match!");
            System.out.println("Error: Passwords do not match.");
            return "AdminSignUp";
        }
        boolean isSaved = adminService.EncryptAndSaveAdmInfo(admin);
        if (!isSaved) {
            model.addAttribute("errorMessage", "Sign-up failed. Please try again!");
            System.out.println("Error: EncryptAndSaveAdmInfo returned false.");
            return "AdminSignUp";
        }
        model.addAttribute("successMessage", "Account created successfully!");
        System.out.println("Success: Admin signed up successfully.");
        return "AdminSignUp";
    }

    //get sign in page
    @GetMapping("/SignIn")
    public String getAdmSignInForm()
    {
        return "AdminSignIn";
    }

    @PostMapping("/SignInProcess")
    public String admSignInProcess(@RequestParam("email") String email,
                                   @RequestParam("password") String password,
                                   HttpSession httpSession,
                                   Model model) {
        String errorMessage = adminService.SignInProcessForAdmin(email, password);

        if (!errorMessage.equals("Login successful")) {
            model.addAttribute("errorMessage", errorMessage);
            return "AdminSignIn";
        }

        Admin admin = adminService.findAdmByEmail(email);

        if (admin == null) {
            model.addAttribute("errorMessage", "Account not found.");
            return "AdminSignIn";
        }

        httpSession.setAttribute("id", admin.getId());
        return "redirect:/index/admin/DashBoard";
    }

    //admin log out
    @GetMapping("/AdmLogOut")
    public String admLogOut(HttpSession httpSession)
    {
        httpSession.setAttribute("id",null);
        return "redirect:/index/admin/DashBoard";
    }

    //get admin user info page
    @GetMapping("/UserInfo")
    public String getUserInfoPage(Model model,HttpSession httpSession)
    {
        Long adminId = (Long) httpSession.getAttribute("id");
        if (adminId != null) {
            Admin admin = adminService.findAdminById(adminId);
            model.addAttribute("admin", admin);
            model.addAttribute("isLogin", true);
            model.addAttribute("user",patientService.findUserByUnVerifiedStatus());
        } else {
            model.addAttribute("isLogin", false);
        }
        return "AdminUserInfo";
    }

    //get change user status (true)
    @GetMapping("/AcceptUser")
    public String changeUserStatus(@RequestParam("id")Long id){
        patientService.changeVerifiedStatus(id);
        return "redirect:/index/admin/UserInfo";
    }

    ////get change user status (false)
    @GetMapping("/RejectUser")
    public String rejectUser(@RequestParam("id")Long id){
        patientService.rejectRequest(id);
        return "redirect:/index/admin/UserInfo";
    }

    //get the doctor info page
    @GetMapping("/DoctorInfo")
    public String getDoctorInfoPage(Model model,HttpSession httpSession){
        Long adminId = (Long) httpSession.getAttribute("id");
        if(adminId != null){
            Admin admin = adminService.findAdminById(adminId);
            model.addAttribute("admin", admin);
            model.addAttribute("isLogin", true);
            model.addAttribute("doctor", doctorService.findDoctorByUnverified());
        }
        else {
            model.addAttribute("isLogin",false);
        }
        return "AdminDoctorInfo";
    }

    //change doctor status(true)
    @GetMapping("/AcceptDoctor")
    public String changDoctorStatus(@RequestParam("id")Long id){
        doctorService.changeVerifiedStatus(id);
        return "redirect:/index/admin/DoctorInfo";
    }

    //change doctor status(reject)
    @GetMapping("/RejectDoctor")
    public String rejectDoctor(@RequestParam("id")Long id){
        doctorService.deleteDoctorById(id);
        return "redirect:/index/admin/DoctorInfo";
    }


    //get the page for doctor information
    @GetMapping("/addDoctorInfo")
    public String addDoctorInfoPage(@RequestParam("doctorId")Long doctorId,Model model,HttpSession httpSession)
    {
        Long adminId = (Long) httpSession.getAttribute("id");
        if (adminId != null && doctorId != null) {
            Admin admin = adminService.findAdminById(adminId);
            model.addAttribute("admin", admin); // email
            model.addAttribute("doctor",doctorService.findDoctorById(doctorId));
            model.addAttribute("doctorName",doctorService.findDoctorById(doctorId).getName());
            model.addAttribute("isLogin", true);
            model.addAttribute("doctorInfo",new DoctorInfo());
        } else {
            model.addAttribute("isLogin", false);
        }
        return "AdminDoctorInfoAddPage";
    }

    @GetMapping("/showAllDoctor")
    public String getShowAllDoctorPage(Model model, HttpSession httpSession) {
        Long adminId = (Long) httpSession.getAttribute("id");
        if (adminId != null) {
            Admin admin = adminService.findAdminById(adminId);
            model.addAttribute("admin", admin);
            model.addAttribute("isLogin", true);

            List<Doctor> doctorList = doctorService.findDoctorByVerified();
            Map<Long, Boolean> doctorInfoExistMap = new HashMap<>();

            for (Doctor doctor : doctorList) {
                boolean isInfoExist = doctorInfoService.checkDoctorInfo(doctor.getId());
                doctorInfoExistMap.put(doctor.getId(), isInfoExist);
            }

            model.addAttribute("doctorList", doctorList);
            model.addAttribute("doctorInfoExistMap", doctorInfoExistMap);
        } else {
            model.addAttribute("isLogin", false);
        }
        return "AdminShowAllDoctorPage";
    }


    @GetMapping("/UpdateDoctorInfoPage")
    public String updateDoctorInfoPage(@RequestParam("doctorId")Long doctorId,Model model,HttpSession httpSession)
    {
        Long adminId = (Long) httpSession.getAttribute("id");
        if (adminId != null && doctorId != null) {
            Admin admin = adminService.findAdminById(adminId);
            model.addAttribute("admin", admin);
            model.addAttribute("doctor",doctorService.findDoctorById(doctorId));
            model.addAttribute("doctorName",doctorService.findDoctorById(doctorId).getName());
            model.addAttribute("doctorInfo",doctorInfoService.findDoctorInfoByDoctorId(doctorId).get());
            model.addAttribute("isLogin", true);
        } else {
            model.addAttribute("isLogin", false);
        }
        return "AdminDoctorUpdateForm";
    }

    @PostMapping("/updateDoctorInfo")
    public String updateDoctorInfo(@RequestParam("doctorId")Long doctorId,
                                   @ModelAttribute("doctorInfo")DoctorInfo updateDoctorInfo,    HttpSession httpSession,
                                   @RequestParam("file")MultipartFile profilePhoto,
                                   Model model) {
        if (doctorId != null) {
            updateDoctorInfo.setDoctor(doctorService.findDoctorById(doctorId));
            try {

                if (doctorId != null) {
                    if (!profilePhoto.isEmpty()) {
                        updateDoctorInfo.setProfilePhoto(profilePhoto.getBytes());
                    }
                    doctorInfoService.updateDoctorInfo(updateDoctorInfo, doctorId);
                    model.addAttribute("successMessage", "Doctor information updated successfully!");
                } else {
                    model.addAttribute("errorMessage", "Doctor ID is missing. Cannot update information.");
                    return "AdminDoctorUpdateForm";
                }
            } catch (IOException e) {
                model.addAttribute("errorMessage", "Error updating doctor information. Please try again.");
                e.printStackTrace();
                return "AdminDoctorUpdateForm";
            } catch (IllegalArgumentException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "AdminDoctorUpdateForm";
            }
        }
        return "redirect:/index/admin/showAllDoctor";
    }

    @PostMapping("/saveDoctorInfo")
    public String saveDoctorInfo(@RequestParam("doctorId")Long doctorId,
                                 @ModelAttribute("doctorInfo")DoctorInfo doctorInfo,
                                 HttpSession httpSession,
                                 @RequestParam("file") MultipartFile profilePhoto,
                                 Model model) throws IOException {
        if(doctorId != null)
        {
            doctorInfo.setDoctor(doctorService.findDoctorById(doctorId));
            try {
                if (!profilePhoto.isEmpty()) {
                    doctorInfo.setProfilePhoto(profilePhoto.getBytes());
                }
                doctorService.findDoctorById(doctorId).setDoctorInfo(doctorInfo);
                doctorInfoService.saveDoctorInfo(doctorInfo);
                model.addAttribute("successMessage", "Doctor information saved successfully!");
            } catch (IOException e) {
                model.addAttribute("errorMessage", "Error saving doctor information. Please try again.");
                e.printStackTrace();
                return "AdminDoctorInfoAddPage";
            }
        }
        return "redirect:/index/admin/showAllDoctor";
    }

    @GetMapping("/deleteDoctorInfo")
    public String deleteDoctorInfo(@RequestParam("doctorId")Long doctorId)
    {
        doctorInfoService.deleteDoctorInfoById(doctorId);
        return "redirect:/index/admin/showAllDoctor";
    }










}

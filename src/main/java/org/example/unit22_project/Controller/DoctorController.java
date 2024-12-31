package org.example.unit22_project.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Model.DoctorInfo;
import org.example.unit22_project.Model.DutyDate;
import org.example.unit22_project.Model.DutyTime;
import org.example.unit22_project.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/index/doctor")
public class DoctorController
{
    private final DoctorService doctorService;

    private final DoctorInfoService doctorInfoService;

    private final DutyDateService dutyDateService;

    private final DutyTimeService dutyTimeService;

    private final AppointmentService appointmentService;

    @Autowired
    public DoctorController(DoctorService doctorService,
                            DoctorInfoService doctorInfoService,
                            DutyDateService dutyDateService,
                            DutyTimeService dutyTimeService,
                            AppointmentService appointmentService){
        this.doctorService = doctorService;
        this.doctorInfoService = doctorInfoService;
        this.dutyDateService = dutyDateService;
        this.dutyTimeService = dutyTimeService;
        ;this.appointmentService = appointmentService;
    }

    @GetMapping("/SignUp")
    public String getDoctorSignUpPage(Model model)
    {
        model.addAttribute("newDoctor",new Doctor());
        return "DoctorSignUp";
    }

    @PostMapping("/SignUpProcess")
    public String signUpProcess(@ModelAttribute("newDoctor") Doctor doctor, Model model) {
        if (!doctorService.isEmailAvailable(doctor.getEmail())) {
            model.addAttribute("errorMessage", "Email already exists!");
            return "DoctorSignUp";
        }
        if (!doctorService.passwordsMatch(doctor)) {
            model.addAttribute("errorMessage", "Passwords do not match!");
            return "DoctorSignUp";
        }
        boolean isSaved = doctorService.encodeAndSaveDoctorInfo(doctor);
        if (!isSaved) {
            model.addAttribute("errorMessage", "Sign-up failed. Please try again!");
            return "DoctorSignUp";
        }
        model.addAttribute("successMessage", "Account created successfully! Wait for Verified");
        return "DoctorSignUp";
    }

    @GetMapping("/SignIn")
    public String getDoctorSignInPage(){
        return "DoctorSignIn";
    }

    @PostMapping("/SignInProcess")
    public String signInProcess(@RequestParam("email")String email,
                                @RequestParam("password")String password,
                                Model model, HttpSession httpSession)
    {
        String errorMessage = doctorService.signInProcess(email,password);
        if(errorMessage != null){
            model.addAttribute("errorMessage",errorMessage);
            return "DoctorSignIn";
        }
        Doctor doctor = doctorService.findDoctorWithEmail(email);
        httpSession.setAttribute("doctorId",doctor.getId());
        return "redirect:/index/doctor/DashBoard";
    }

    @GetMapping("/DashBoard")
    public String getDoctorDashBoard(Model model,HttpSession httpSession)
    {
        Long doctorId = (Long) httpSession.getAttribute("doctorId");
        if(doctorId != null)
        {
            Doctor doctor = doctorService.findDoctorById(doctorId);
            model.addAttribute("isLogIn",true);
            model.addAttribute("doctor",doctor);
            model.addAttribute("totalAppointmentCount",doctorService.getAppointmentCountByDoctorId(doctorId));
            model.addAttribute("waitedAppointmentCount",doctorService.getAppointmentCountByStatusAndId("pending",doctorId));
            model.addAttribute("appointmentList",appointmentService.getAppointmentListByDoctorIdAndStatus(doctorId));
        }
        else {
            model.addAttribute("isLogIn",false);
        }
        return "DoctorDashBoard";
    }

    @GetMapping("/changeAppointmentStatusCheckOut")
    public String changeAppointmentStatus(@RequestParam("appointmentId")Long appointmentId)
    {
        appointmentService.changeAppointmentStatus(appointmentId);
        return "redirect:/index/doctor/DashBoard";
    }

    @GetMapping("/changeAppointmentStatusAccept")
    public String changeAppointmentStatusAccpet(@RequestParam("appointmentId")Long appointmentId){
        appointmentService.changeAppointmentStatusAccept(appointmentId);
        return "redirect:/index/doctor/DashBoard";
    }

    @GetMapping("/DoctorLogOut")
    public String LogOutProcess(HttpSession httpSession)
    {
        httpSession.setAttribute("doctorId",null);
        return "redirect:/index/doctor/DashBoard";
    }

    @GetMapping("/UpdateForm")
    public String getDoctorInform(HttpSession httpSession,Model model)
    {
        Long doctorId = (Long) httpSession.getAttribute("doctorId");
        if(doctorId != null){
            Doctor doctor = doctorService.findDoctorById(doctorId);
            model.addAttribute("isLogIn",true);
            model.addAttribute("doctor",doctor);
            model.addAttribute("doctorInfo",new DoctorInfo());
        }
        else {
            model.addAttribute("isLogIn",false);
        }
        return "DoctorUpdateForm";
    }

    @PostMapping("/update")
    public String updateDoctorInfo(@RequestParam("email")String email,
                                   @RequestParam("address")String address,
                                   @RequestParam("phNumber")String phNumber,
                                   @RequestParam("feesRate")double feesRate,
                                   HttpSession httpSession,Model model){
        Long doctorId = (Long) httpSession.getAttribute("doctorId");
        if(doctorId != null)
        {
            DoctorInfo doctorInfo = doctorInfoService.findDoctorInfoByDoctorId(doctorId).get();
            doctorInfoService.updateDoctorInfo(doctorId,email,phNumber,address,feesRate);
            return "redirect:/index/doctor/Profile";
        }

        model.addAttribute("errorMessage","Error Occured");
        return "DoctorUpdateForm";
    }

//    @PostMapping("/saveDoctorInfo")
//    public String saveDoctorInfo(@ModelAttribute("doctorInfo")DoctorInfo doctorInfo,
//                                 HttpSession httpSession,
//                                 @RequestParam("file") MultipartFile profilePhoto,
//                                 Model model) throws IOException {
//        Long doctorId = (Long) httpSession.getAttribute("doctorId");
//        if (doctorId != null)
//        {
//            doctorInfo.setDoctor(doctorService.findDoctorById(doctorId));
//            try {
//                if (!profilePhoto.isEmpty()) {
//                    doctorInfo.setProfilePhoto(profilePhoto.getBytes());
//                }
//                doctorService.findDoctorById(doctorId).setDoctorInfo(doctorInfo);
//                doctorInfoService.saveDoctorInfo(doctorInfo);
//                model.addAttribute("successMessage", "Doctor information saved successfully!");
//            } catch (IOException e) {
//                model.addAttribute("errorMessage", "Error saving doctor information. Please try again.");
//                e.printStackTrace();
//            }
//        }
//        return "DoctorInfoForm";
//    }

    @GetMapping("/Profile")
    public String getDoctorProfile(HttpSession httpSession,Model model)
    {
        Long doctorId = (Long) httpSession.getAttribute("doctorId");
        if(doctorId != null){
            model.addAttribute("isLogIn",true);
            System.out.println(doctorInfoService.findDoctorInfoByDoctorId(doctorId).toString());
            model.addAttribute("doctor",doctorService.findDoctorById(doctorId));
            model.addAttribute("doctorInfo",doctorInfoService.findDoctorInfoByDoctorId(doctorId));
        }
        else {
            model.addAttribute("isLogIn",false);
        }
        return "DoctorProfile";
    }

    @GetMapping("/Password")
    public String getDoctorPasswordRestPage(HttpSession httpSession,Model model)
    {
        Long doctorId = (Long) httpSession.getAttribute("doctorId");
        if(doctorId != null)
        {
            Doctor doctor = doctorService.findDoctorById(doctorId);
            model.addAttribute("isLogIn",true);
            model.addAttribute("doctor",doctor);
        }
        else {
            model.addAttribute("isLogIn",false);
        }
        return "DoctorRestPassword";
    }

    @PostMapping("/ResetPasswordProcess")
    public String restPasswordProcess(@RequestParam("oldPassword")String oldPassword,
                                      @RequestParam("newPassword")String newPassword,
                                      @RequestParam("reEnterNewPassword")String reEnterNewPassword,
                                      HttpSession httpSession,Model model)
    {
        Long doctorId = (Long) httpSession.getAttribute("doctorId");
        String errorMessage = doctorService.changePassword(doctorId,oldPassword,newPassword,reEnterNewPassword);
        if(!errorMessage.equals("Reset Password is Done")){
            model.addAttribute("errorMessage",errorMessage);
        }
        model.addAttribute("successMessage",errorMessage);
        return "DoctorRestPassword";
    }

    @GetMapping("/addDutyDate")
    public String getAddDutyDatePage(Model model,
                                     HttpSession httpSession) {
        Long doctorId = (Long) httpSession.getAttribute("doctorId");
        if (doctorId != null) {
            Doctor doctor = doctorService.findDoctorById(doctorId);
            model.addAttribute("isLogIn", true);
            model.addAttribute("doctor", doctor);
            model.addAttribute("dutyDate", new DutyDate());
            model.addAttribute("dateList", dutyDateService.showAllDutyDateByDoctorId(doctorId));
        } else {
            model.addAttribute("isLogIn", false);
        }
        return "DoctorAddDutyDate";
    }

    @PostMapping("/saveDuty_date")
    public String saveDutyDate(@ModelAttribute("dutyDate") DutyDate dutyDate,
                               HttpSession httpSession, Model model,
                               RedirectAttributes redirectAttributes) {
        Long doctorId = (Long) httpSession.getAttribute("doctorId");
        String returnMessage = null;
        if (doctorId != null) {
            returnMessage = dutyDateService.saveDutyDate(dutyDate, doctorId);
            model.addAttribute("isLogIn", false);
            return "redirect:/index/doctor/addDutyDate?message=" + returnMessage;
        } else {
            model.addAttribute("isLogIn", false);
            return "DoctorAddDutyDate";
        }
    }



    @GetMapping("/addDutyTime")
    public String addDutyTimePage(HttpSession httpSession,@RequestParam("dutyDateId")Long dutyDateId,Model model)
    {
        Long doctorId = (Long) httpSession.getAttribute("doctorId");
        if(dutyDateId != null)
        {
            Doctor doctor = doctorService.findDoctorById(doctorId);
            model.addAttribute("dutyDateName",dutyDateService.findDutyDateById(dutyDateId));
            System.out.println(dutyDateId);
            model.addAttribute("isLogin",true);
            model.addAttribute("doctor",doctor);
            model.addAttribute("dutyDateId",dutyDateId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            List<DutyTime> dutyTimeList = dutyTimeService.findDutyTimeByDoctorIdAndDutyDateId(dutyDateId,doctorId);
            model.addAttribute("dutyTimeList", dutyTimeList );
        }
        else {
            model.addAttribute("isLogin",false);
        }
        return "DoctorAddDutyTime";
    }

    @GetMapping("/deleteByDutyDateId")
    public String deleteDutyDateById(@RequestParam("dutyDateId")Long dutyDateId){
        dutyDateService.deleteDutyDateById(dutyDateId);
        return "redirect:/index/doctor/addDutyDate";
    }

    @GetMapping("/deleteByDutyTimesId")
    public String deleteDutyTimesById(@RequestParam("dutyTimesId")Long dutyTimesId,@RequestParam("dutyDateId")Long dutyDateId)
    {
        dutyTimeService.deleteDutyTimesById(dutyTimesId);
        return "redirect:/index/doctor/addDutyTime?dutyDateId="+dutyDateId;
    }

    @GetMapping("/profilePhoto")
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePhoto(HttpSession httpSession)
    {
        Long doctorId = (Long) httpSession.getAttribute("doctorId");
        byte[] image = doctorInfoService.findDoctorInfoByDoctorId(doctorId).get().getProfilePhoto();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @PostMapping("/saveDutyTime")
    public String saveDutyTime(@RequestParam("dutyDateId") Long dutyDateId,
                               @RequestParam("dutyTime") LocalTime dutyTimeStr,
                               @RequestParam("offTime") LocalTime offTimeStr,
                               Model model,RedirectAttributes redirectAttributes) {
        if (dutyDateId != null) {
            String returnMessage = dutyTimeService.addDutyTime(dutyDateId, dutyTimeStr, offTimeStr);
            if (!"Saved successfully!".equals(returnMessage)) {
                redirectAttributes.addFlashAttribute("errorMessage", returnMessage);
                model.addAttribute("dutyDateId", dutyDateId);
                return  "redirect:/index/doctor/addDutyTime?dutyDateId=" + dutyDateId;
            }
        }
        return "redirect:/index/doctor/addDutyTime?dutyDateId=" + dutyDateId;
    }




}

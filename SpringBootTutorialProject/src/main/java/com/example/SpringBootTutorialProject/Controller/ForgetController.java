package com.example.SpringBootTutorialProject.Controller;

import com.example.SpringBootTutorialProject.Dao.UserRepository;
import com.example.SpringBootTutorialProject.Entity.User;
import com.example.SpringBootTutorialProject.Helper.Message;
import com.example.SpringBootTutorialProject.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgetController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private EmailService emailService;
    Random random=new Random();

    @RequestMapping("/forget")
    public String openEmailForm() {
        return "forgetPassword";
    }
    @PostMapping("/send_OTP")
    public String sendOTP(@RequestParam("email") String email, HttpSession session) {
        System.out.println("EMAIL" + email);

        // Generating 4-digit OTP code
        int otpValue = random.nextInt(9999); // Not exceeding 9999
        System.out.println("OTP" + otpValue);

//        // Create an instance of OTP entity and set the OTP value
//        User otp = new User(otpValue);
//        userRepository.save(otp); // Save the OTP entity into the database
//
//        // Write code to send the OTP to the email

        String subject = "OTP from SCM";
        String message = "The OTP Number is: " + otpValue;
        String to = email;

        boolean flag = this.emailService.sendEmail(subject, message, to);
        if (flag=true) {
            session.setAttribute("Myotp",otpValue);
            session.setAttribute("email",email);//we can fetch the email till the session valid
            return "verify";
        } else {
            // Set the message object in the session
            Message messages = new Message("Check your email for OTP.", "success");
            session.setAttribute("message", messages);
            return "forgetPassword";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp,HttpSession session){
        //getting the otp from session
        int Myotp=(int) session.getAttribute("Myotp");
        String email=(String) session.getAttribute("email");
        if(Myotp==otp){
            User user= userRepository.getUserByUserName(email);
            if(user==null){
                session.setAttribute("message","First Create your account");
                return "Signup";
            }
            else {
                return "changePassword";
            }
        }
        else{
            session.setAttribute("message","OTP Dosen't Match !!!");
            return "verify";
        }

    }
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword")String newPassword, HttpSession session){
        String email=(String) session.getAttribute("email");
        User user=userRepository.getUserByUserName(email);
        if(user==null){
            session.setAttribute("message","First Create your account");
            return "Signup";
        }
        else{
            user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(user);
            session.setAttribute("message","Update Password Successfully !!");
        }
        return "signin";
    }

}

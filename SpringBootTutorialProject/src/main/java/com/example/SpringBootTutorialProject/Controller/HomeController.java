package com.example.SpringBootTutorialProject.Controller;

import com.example.SpringBootTutorialProject.Dao.UserRepository;
import com.example.SpringBootTutorialProject.Entity.User;
import com.example.SpringBootTutorialProject.Helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title","home- Smart Contract Manager");
        return "home";
    }
    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","about-Smart Contract Manager");
        return "about";
    }
    @RequestMapping("/Signup")
    public String Signup(Model model){
        model.addAttribute("title","Signup-Smart Contract Manager");
        model.addAttribute("user", new User());
        return "Signup";
    }
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String addUser(@Valid @ModelAttribute("user") User user,BindingResult result, Model model,  HttpSession session){
        try{
            if (result.hasErrors()){
                System.out.println("ERROR"+result.toString());
                model.addAttribute("user",user);
                session.setAttribute("message", new Message("Please Validation is required for Account Creation","alert Danger"));
                return "Signup";
            }
            user.setRole("ROLE_USER");

//            password encoding while registering
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            session.setAttribute("message", new Message("Register Successfully","alert Success"));
            model.addAttribute("user",new User());
            return "Signup";
        }
        catch (Exception e){
            model.addAttribute("user",user);
            session.setAttribute("message", new Message("Something Went Wrong","alert Danger"));
            return "Signup";
        }
    }
    @RequestMapping("/signin")
    public String login(Model model){
        model.addAttribute("title","Login-Smart Contract Manager");
        return "signin";
    }

}


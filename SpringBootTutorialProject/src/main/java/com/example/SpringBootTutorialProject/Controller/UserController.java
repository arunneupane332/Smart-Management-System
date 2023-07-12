package com.example.SpringBootTutorialProject.Controller;

import com.example.SpringBootTutorialProject.Dao.ContractRepository;
import com.example.SpringBootTutorialProject.Dao.UserRepository;
import com.example.SpringBootTutorialProject.Entity.Contract;
import com.example.SpringBootTutorialProject.Entity.User;
import com.example.SpringBootTutorialProject.Helper.Message;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractRepository contractRepository;
    //Dashboard of user
    @RequestMapping("/index")
    public String user(Model model, Principal principal,@ModelAttribute User user) {
        String userName = principal.getName();
        User users = userRepository.getUserByUserName(userName);
        model.addAttribute("user", user);
        return "normal/UserDashboard"; // Return the view name
    }
    //common variable data
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {

        // Retrieve the username of the currently logged-in user
        String userName = principal.getName();

        // Retrieve the user details from the repository using the username
        User user = userRepository.getUserByUserName(userName);

        // Add the user object to the model if needed for rendering the view
        model.addAttribute("user", user);
    }

    @GetMapping("/add-contract")
    public String openAddContactForm(Model model){
        model.addAttribute("title","Add Contract");
        model.addAttribute("contract",new Contract());
        return "normal/addContract";
    }

    @RequestMapping(value = "/process-contract",
//            headers = "content-type=multipart/*",
            method = RequestMethod.POST)
//    @PostMapping(value="/process-contract", headers = "content-type=multipart/*",  = "RequestMethod.POST")
    public String processContract(@Valid @ModelAttribute("contract") Contract contract,
                                  Principal principal,
                                  Model model,HttpSession session) {
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            contract.setUser(user);
            //to make the relationship which user has save the contract details
            user.getContract().add(contract);

            //Processing and Uploding the file
//            if(file.isEmpty()){
//                System.out.println("File Doesn't exist.");
//            }
//            else{
//                contract.setImage(file.getOriginalFilename());
//                File saveFile=new ClassPathResource("static/img").getFile();
//                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
//                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
//            }

            userRepository.save(user);
            session.setAttribute("message", new Message("Added successfully","alert Success"));
            System.out.println("DATA" + contract);
            System.out.println("successfully store in the database");
            return "normal/addContract";
        }
        catch(Exception e){
            model.addAttribute("contract",contract);
            session.setAttribute("message", new Message("Something Went Wrong","alert Danger"));
            return "normal/addContract";
            }
    }
    @SneakyThrows
    @GetMapping("/show-contract/{page}")
    public String showContract(@PathVariable("page") Integer page, Model model,Principal principal) throws Exception {
        try {
            model.addAttribute("Title", "Show contact");
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            Pageable pageable = PageRequest.of(page, 10);
            Page<Contract> contracts = this.contractRepository.findContractByUser(user.getId(), pageable);
            model.addAttribute("contracts", contracts);
            model.addAttribute("currentpage", page);
            model.addAttribute("totalpages", contracts.getTotalPages());
            return "normal/show_contracts";
        }
        catch (Exception e){
            throw new Exception("Something went Wrong!!");
        }
    }

    //deleting the user
    @GetMapping("delete/{Cid}")
    public String deleteContract(@PathVariable("Cid")Integer cId,@ModelAttribute Contract contract, Principal principal, Model model,HttpSession session){
        try {
            Optional<Contract> contractOptional = this.contractRepository.findById(cId);
            Contract contracts = contractOptional.get();
            User user=this.userRepository.getUserByUserName(principal.getName());
            user.getContract().remove(contract);
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Delete successfully", "alert Success"));
            return "redirect:/user/show-contract/0";
        }
        catch (Exception e){
            session.setAttribute("message", new Message("Something went Wrong!!", "alert Danger"));
            throw new IllegalArgumentException("Something went Wrong");
        }
    }
    @PostMapping("update-contract/{Cid}")
    public String updateContract(@PathVariable("Cid") Integer cId,Model  model,Principal principal,HttpSession session){
            model.addAttribute("title", "Update Contract");
            Contract contract = this.contractRepository.findById(cId).get();
            model.addAttribute("contract", contract);
            User user = this.userRepository.getUserByUserName(principal.getName());
            //new user seting
            contract.setUser(user);
            this.contractRepository.save(contract);
            return "normal/updateForm";
        }


    ///updating the user
    @RequestMapping(value="/update-process",method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contract contract,Principal principal,HttpSession session) {
            //getting the user
            User user = this.userRepository.getUserByUserName(principal.getName());
            //new user setting
            contract.setUser(user);
            this.contractRepository.save(contract);
            session.setAttribute("message", new Message("Updated successfully", "alert Success"));
            return "redirect:/user/show-contract/0";
    }




   //To update the profile
    @RequestMapping(value = "edit-profile",method = RequestMethod.POST)
    public String updateProfile(@Valid @ModelAttribute("user") User user, Model model,Principal principal,HttpSession session) {
        try {
            model.addAttribute("title", "edit profile");
            model.addAttribute("user", user);
            //get the user
            String userName = principal.getName();
            //match it with the user id and get the user
            User users = this.userRepository.getUserByUserName(userName);
            this.userRepository.save(users);
//            session.setAttribute("message", new Message("edit successfully!!", "alert success"));
            return "normal/editProfile";

        } catch (Exception e) {
            session.setAttribute("message", new Message("Something went Wrong!!", "alert danger"));
            throw new IllegalArgumentException("Something went Wrong");
        }
    }



//@PostMapping(path = "/setting")
@RequestMapping(value = "/setting", method = {RequestMethod.GET, RequestMethod.POST})
public String addSetting(String oldPassword, String newPassword, Principal principal, HttpSession session) {
    String userName = principal.getName();
    User user = this.userRepository.getUserByUserName(userName);

//recently we haven't pass the old password to handel the null oldpassword exception we have to declare it.
    if (oldPassword == null || oldPassword.isEmpty()) {
        return "normal/setting";
    }
    if (this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
        //set the password in encode form
        user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
        this.userRepository.save(user);
        session.setAttribute("message", new Message("Change Successful!", "success"));
    } else {
        session.setAttribute("message", new Message("Wrong Old Password!", "danger"));
    }
    return "normal/setting";
}


    @RequestMapping(value = "/profile",
            method = {RequestMethod.GET, RequestMethod.POST})
//    @RequestMapping(value = "/profile", method = {RequestMethod.GET, RequestMethod.POST})
    public String upload() throws IOException {
//        User user = new User();
//        user.setName(file.getOriginalFilename());
//        user.setPhotos(file.getBytes());
//        userRepository.save(user);
        return "normal/profile";
    }
//    public String uploadProfileImage(User user, @RequestParam("imageurl") MultipartFile multipartFile) throws IOException {
//        if(multipartFile.isEmpty()){
//                System.out.println("File Doesn't exist.");
//            }
//            else{
//                user.setPhotos(multipartFile.getOriginalFilename());
//                File saveFile=new ClassPathResource("static/img").getFile();
//                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+multipartFile.getOriginalFilename());
//                Files.copy(multipartFile.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
//            }
//        return "normal/profile";




}

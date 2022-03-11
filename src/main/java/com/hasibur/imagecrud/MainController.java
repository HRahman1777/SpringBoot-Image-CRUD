package com.hasibur.imagecrud;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Controller
public class MainController {

    @Autowired
    private UserRepo repo;

    @GetMapping("/")
    public String homePage(){
        return "index.html";
    }
    @GetMapping("/show")
    public String showPage(Model model){
        //System.out.println(repo.findById(1L));
        model.addAttribute("users", repo.findAll());
        model.addAttribute("defPath", "uploadedPhotos/");
        return "show.html";
    }


    @PostMapping("/users/save")
    public RedirectView saveUser(User user,
                                 @RequestParam("image") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        user.setPhotos(fileName);

        User savedUser = repo.save(user);

        String uploadDir = "src/main/resources/static/uploadedPhotos/" + savedUser.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return new RedirectView("/", true);
    }

    @GetMapping("/file/edit/{id}")
    public String fileEditPage(@PathVariable("id")  Long id, Model model){

        model.addAttribute("user", repo.findById(id).get());


        return "edit.html";
    }

    @PostMapping("/file/edit")
    public String fileEditDone(@RequestParam("sid") String sid, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        Long id = Long.parseLong(sid);
        User user = repo.findById(id).get();

        user.setPhotos(fileName);

        System.out.println(user.getId());
        System.out.println(user.getPhotos());

        repo.save(user);

        String uploadDir = "src/main/resources/static/uploadedPhotos/" + user.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        System.out.println(user.getId());
        System.out.println(user.getPhotos());

        return "redirect:/";
    }


    @GetMapping("/file/delete/{id}")
    public String fileDelete(@PathVariable("id")  Long id){
        repo.deleteById(id);
        return "redirect:/show";
    }

}

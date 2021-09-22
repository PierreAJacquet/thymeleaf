package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Role;
import com.ipiecoles.communes.web.model.User;
import com.ipiecoles.communes.web.repository.RoleRepository;
import com.ipiecoles.communes.web.repository.UserRepository;
import com.ipiecoles.communes.web.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Set;

@Controller
public class UserController {

    @Autowired
    private MyUserDetailService userDetailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) Boolean error,
                        ModelMap model) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("type", "danger");
            model.addAttribute("message", "Erreur lors de la connexion.");

        }
        return "login";
    }


    @GetMapping("/register")
    public String register(ModelMap model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String createNewUser(@Valid User user,
                                BindingResult bindingResult,
                                final ModelMap model,
                                RedirectAttributes attributes) {
        //Vérifier su un User existe déjà avec le même nom
        User userExits = userRepository.findByUserName(user.getUserName());
        //Gérer les erreurs de validation
        if (userExits != null) {
            bindingResult.rejectValue("userName", "error.username", "Nom d'utilisateur déjà pris");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("type", "danger");
            model.addAttribute("message", "Erreur lors de l'inscription de l'utilisateur");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //Affecter le rôle USER...
        Role userRole = roleRepository.findByRole("ROLE_USER");
        user.setRoles(Set.of(userRole));
        //Gérer une validation par email... Ici valide par défaut
        user.setActive(true);
        userRepository.save(user);

        attributes.addFlashAttribute("type", "success");
        attributes.addFlashAttribute("message", "Inscription réussie, vous pouvez vous connecter");
        return "redirect:/login";
    }
}

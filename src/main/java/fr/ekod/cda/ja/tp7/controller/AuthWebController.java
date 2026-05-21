package fr.ekod.cda.ja.tp7.controller;

import fr.ekod.cda.ja.tp7.dto.auth.RegisterRequestDTO;
import fr.ekod.cda.ja.tp7.security.CustomUserDetailsService;
import fr.ekod.cda.ja.tp7.security.JwtService;
import fr.ekod.cda.ja.tp7.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthWebController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response,
            Model model
    ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String token = jwtService.generateAccessToken(userDetails);
            setTokenCookie(response, token);
            return "redirect:/movies";
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Email ou mot de passe incorrect");
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response,
            Model model
    ) {
        try {
            userService.register(new RegisterRequestDTO(firstName, lastName, email, password));
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String token = jwtService.generateAccessToken(userDetails);
            setTokenCookie(response, token);
            return "redirect:/movies";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("ACCESS_TOKEN", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("ACCESS_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) jwtService.getAccessExpirationSeconds());
        response.addCookie(cookie);
    }
}

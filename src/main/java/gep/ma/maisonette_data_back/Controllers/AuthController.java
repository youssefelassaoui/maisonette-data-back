package gep.ma.maisonette_data_back.Controllers;

import gep.ma.maisonette_data_back.Models.User;
import gep.ma.maisonette_data_back.Repos.UserRepository;
import gep.ma.maisonette_data_back.Services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender; // JavaMailSender pour l'envoi d'emails

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (!user.getEmail().endsWith("@greenenergypark.ma")) {
            return ResponseEntity.badRequest().body("Seuls les emails @greenenergypark.ma sont autorisés.");
        }

        // Générer un ID unique et un token
        user.setId(UUID.randomUUID());
        user.setToken(UUID.randomUUID().toString());
        user.setVerified(false);

        // Sauvegarder l'utilisateur
        userRepository.save(user);

        // Envoyer l'email de confirmation
        String confirmationLink = "http://localhost:8083/api/auth/verify?token=" + user.getToken();
        sendEmail(user.getEmail(), "Confirmation de votre compte",
                "Cliquez sur le lien pour confirmer votre email : " + confirmationLink);

        return ResponseEntity.ok("Un email de confirmation a été envoyé à " + user.getEmail());
    }

    /**
     * Endpoint pour valider l'email de l'utilisateur
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        Optional<User> optionalUser = userRepository.findByToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true);
            userRepository.save(user);

            // Extraire le nom de l'email
            String email = user.getEmail();
            String username = email.split("@")[0]; // Prend la partie avant '@'

            // Générer un token JWT
            String jwtToken = jwtService.generateToken(email);

            // Rediriger avec les informations dans l'URL
            String redirectUrl = "http://localhost:3000/login?verified=true&token=" + jwtToken + "&username=" + username;

            // HTTP 302 redirection
            return ResponseEntity.status(302)
                    .header("Location", redirectUrl)
                    .build();
        } else {
            // Redirection en cas d'erreur
            String errorRedirect = "http://localhost:3000/login?verified=false";
            return ResponseEntity.status(302)
                    .header("Location", errorRedirect)
                    .build();
        }
    }



    /**
     * Endpoint pour la connexion (login)
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!user.isVerified()) {
                return ResponseEntity.badRequest().body("Veuillez confirmer votre email avant de vous connecter.");
            }

            if (user.getPassword().equals(loginRequest.getPassword())) {
                String jwtToken = jwtService.generateToken(user.getEmail());
                return ResponseEntity.ok(Map.of("token", jwtToken, "message", "Connexion réussie"));
            } else {
                return ResponseEntity.badRequest().body("Mot de passe incorrect.");
            }
        } else {
            return ResponseEntity.badRequest().body("Utilisateur non trouvé.");
        }
    }



    /**
     * Méthode privée pour envoyer un email via JavaMailSender
     */
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}

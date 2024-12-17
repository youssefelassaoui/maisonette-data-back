package gep.ma.maisonette_data_back.Controllers;

import gep.ma.maisonette_data_back.Models.User;
import gep.ma.maisonette_data_back.Repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender; // JavaMailSender pour l'envoi d'emails

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Vérifier si l'email existe déjà
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Cet email est déjà enregistré.");
        }

        // Générer un ID unique et un token de confirmation
        user.setId(UUID.randomUUID());
        user.setToken(UUID.randomUUID().toString());
        user.setVerified(false);

        // Sauvegarder l'utilisateur dans la base de données
        userRepository.save(user);

        // Envoyer l'email de confirmation
        String confirmationLink = "http://localhost:8083/api/auth/verify?token=" + user.getToken();
        sendEmail(user.getEmail(), "Confirmation de votre compte",
                "Cliquez sur le lien pour confirmer votre email : " + confirmationLink);

        return ResponseEntity.ok("Un email de confirmation a été envoyé.");
    }


    /**
     * Endpoint pour valider l'email de l'utilisateur
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        Optional<User> optionalUser = userRepository.findByToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true); // Met à jour le statut à true
            userRepository.save(user); // Enregistre la mise à jour
            return ResponseEntity.ok("Email confirmé avec succès !");
        } else {
            return ResponseEntity.badRequest().body("Token invalide ou expiré.");
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

            // Vérifier si l'email est confirmé
            if (!user.isVerified()) {
                return ResponseEntity.badRequest().body("Veuillez confirmer votre email avant de vous connecter.");
            }

            // Vérifier le mot de passe
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok("Connexion réussie pour : " + user.getEmail());
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

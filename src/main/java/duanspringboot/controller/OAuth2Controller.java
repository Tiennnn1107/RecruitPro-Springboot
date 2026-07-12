package duanspringboot.controller;

import duanspringboot.dto.auth.AuthResponse;
import duanspringboot.entity.User;
import duanspringboot.enums.Role;
import duanspringboot.repository.UserRepository;
import duanspringboot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/google/success")
    public ResponseEntity<?> handleGoogleSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "OAuth2 user not found"));
        }

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");

        if (email == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email not provided by Google"));
        }

        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = User.builder()
                    .email(email)
                    .password("")
                    .role(Role.CANDIDATE)
                    .isActive(true)
                    .build();
            user = userRepository.save(user);
        }

        AuthResponse authResponse = authService.generateTokenForUser(user);

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/failure")
    public ResponseEntity<?> handleFailure() {
        return ResponseEntity.badRequest().body(Map.of("error", "OAuth2 login failed"));
    }
}

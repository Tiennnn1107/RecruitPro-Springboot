package duanspringboot.controller;

import duanspringboot.dto.auth.AuthResponse;
import duanspringboot.dto.auth.ForgotPasswordRequest;
import duanspringboot.dto.auth.LoginRequest;
import duanspringboot.dto.auth.RegisterRequest;
import duanspringboot.dto.auth.ResetPasswordRequest;
import duanspringboot.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.base-url:}")
    private String appBaseUrl;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String resetBaseUrl = getResetBaseUrl();
        authService.requestPasswordReset(request, resetBaseUrl);
        return ResponseEntity.ok(Map.of("message", "Neu email ton tai, he thong da gui link dat lai mat khau."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Dat lai mat khau thanh cong. Vui long dang nhap lai."));
    }

    private String getResetBaseUrl() {
        if (appBaseUrl != null && !appBaseUrl.isBlank()) {
            return appBaseUrl.replaceAll("/+$", "") + "/reset-password";
        }

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/reset-password")
                .toUriString();
    }
}

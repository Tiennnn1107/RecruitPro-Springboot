package duanspringboot.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import duanspringboot.dto.auth.AuthResponse;
import duanspringboot.dto.auth.ForgotPasswordRequest;
import duanspringboot.dto.auth.LoginRequest;
import duanspringboot.dto.auth.RegisterRequest;
import duanspringboot.dto.auth.ResetPasswordRequest;
import duanspringboot.entity.PasswordResetToken;
import duanspringboot.enums.Role;
import duanspringboot.repository.PasswordResetTokenRepository;
import duanspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import duanspringboot.entity.User;
import duanspringboot.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email đã tồn tại");

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String message = null;
        if (request.getRole() == Role.RECRUITER) {
            message = "Đăng ký thành công! Vui lòng cung cấp thông tin công ty để được phê duyệt.";
        }
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message(message)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse generateTokenForUser(User user) {
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest request, String resetBaseUrl) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            passwordResetTokenRepository.deleteByUser(user);

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(UUID.randomUUID().toString())
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusMinutes(30))
                    .build();
            passwordResetTokenRepository.save(resetToken);

            String resetLink = resetBaseUrl + "?token=" + resetToken.getToken();
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Link dat lai mat khau khong hop le"));

        if (resetToken.getUsedAt() != null) {
            throw new RuntimeException("Link dat lai mat khau da duoc su dung");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link dat lai mat khau da het han");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);
    }
}

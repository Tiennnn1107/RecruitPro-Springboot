package duanspringboot.config;

import duanspringboot.entity.User;
import duanspringboot.enums.Role;
import duanspringboot.repository.UserRepository;
import duanspringboot.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        if (email == null) {
            sendError(response, "Email not provided by Google");
            return;
        }

        // Find or create user
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

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Determine redirect URL based on role
        String role = user.getRole().name();
        String redirectUrl = "/";

        if ("ADMIN".equals(role)) {
            redirectUrl = "/admin/dashboard";
        } else if ("RECRUITER".equals(role) || "COMPANY".equals(role)) {
            redirectUrl = "/recruiter/jobs";
        }

        // Instead of returning raw JSON, return an HTML page with a script that saves attributes to localStorage/Cookie and redirects
        response.setContentType("text/html;charset=UTF-8");
        
        String userInfoJson = String.format("{\"userId\":%d,\"email\":\"%s\",\"username\":\"%s\",\"role\":\"%s\"}",
                user.getId(), user.getEmail(), user.getEmail().split("@")[0], user.getRole().name());

        String html = "<!DOCTYPE html><html><head><title>Đăng nhập thành công</title></head><body>"
                + "<script>"
                + "localStorage.setItem('jwt_token', '" + token + "');"
                + "document.cookie = 'jwt_token=" + token + "; path=/; max-age=86400; SameSite=Lax' + (window.location.protocol === 'https:' ? '; Secure' : '');"
                + "localStorage.setItem('user_info', '" + userInfoJson + "');"
                + "window.location.replace('" + redirectUrl + "');"
                + "</script>"
                + "</body></html>";
                
        response.getWriter().write(html);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        objectMapper.writeValue(response.getWriter(), error);
    }
}

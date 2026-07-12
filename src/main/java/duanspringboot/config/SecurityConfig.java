package duanspringboot.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import duanspringboot.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Value("${app.cors.allowed-origins:http://localhost:8080,http://localhost:3000,http://127.0.0.1:8080,http://127.0.0.1:3000,http://recruit-alb-1002681411.ap-southeast-1.elb.amazonaws.com,https://d32lxiso1iao8j.cloudfront.net}")
    private String allowedOrigins;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/error", "/static/**",
                        "/resources/**", "/uploads/**", "/assets/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/files/upload").authenticated()

                        // Cho phép truy cập View HTML
                        .requestMatchers("/", "/login", "/register", "/forgot-password", "/reset-password", "/jobs/**", "/notifications",
                                "/css/**", "/js/**", "/images/**", "/assets/**")
                        .permitAll()

                        // API public
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/files/view").permitAll()
                        .requestMatchers("/api/jobs/search").permitAll()
                        .requestMatchers("/api/jobs/search/paginated").permitAll()
                        .requestMatchers("/api/jobs/public/**").permitAll()
                        .requestMatchers("/api/jobs/{id}").permitAll()
                        .requestMatchers("/api/job-fields/**").permitAll()
                        .requestMatchers("/api/blogs/**").permitAll()

                        // Phân quyền API
                        .requestMatchers("/api/candidate/**").hasRole("CANDIDATE")
                        .requestMatchers("/api/companies/**").hasRole("RECRUITER")
                        .requestMatchers("/api/jobs/**").hasRole("RECRUITER")
                        .requestMatchers("/api/applications/**").authenticated()
                        .requestMatchers("/api/interviews/**").hasRole("RECRUITER")
                        .requestMatchers("/api/recruiter-approval/**").hasRole("RECRUITER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/recruiter-approvals/**").hasRole("ADMIN")
                        .requestMatchers("/api/messages/**").authenticated()

                        .requestMatchers("/candidate/**").hasRole("CANDIDATE")
                        .requestMatchers("/recruiter/**").hasRole("RECRUITER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            String path = request.getServletPath();
                            if (path.startsWith("/api/")) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                            } else {
                                response.sendRedirect("/login");
                            }
                        }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
        config.setAllowedOriginPatterns(origins);
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

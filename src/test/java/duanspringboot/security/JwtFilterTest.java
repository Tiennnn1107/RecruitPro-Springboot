package duanspringboot.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import duanspringboot.util.JwtUtil;
import jakarta.servlet.FilterChain;

class JwtFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void jwtReplacesAuthenticationLeftByPreviousAccountSession() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        JwtFilter filter = new JwtFilter(jwtUtil, userDetailsService);
        FilterChain chain = mock(FilterChain.class);

        UserDetails previousCandidate = new User("candidate@test.com", "", List.of(
                new SimpleGrantedAuthority("ROLE_CANDIDATE")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(previousCandidate, null, previousCandidate.getAuthorities()));

        UserDetails recruiter = new User("recruiter@test.com", "", List.of(
                new SimpleGrantedAuthority("ROLE_RECRUITER")));
        when(jwtUtil.extractEmail("new-token")).thenReturn("recruiter@test.com");
        when(userDetailsService.loadUserByUsername("recruiter@test.com")).thenReturn(recruiter);
        when(jwtUtil.validateToken("new-token", "recruiter@test.com")).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/recruiter/jobs");
        request.addHeader("Authorization", "Bearer new-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("recruiter@test.com");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_RECRUITER");
        verify(chain).doFilter(request, response);
    }
}

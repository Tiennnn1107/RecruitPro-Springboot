package duanspringboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void sendInterviewInvitation(String toEmail, String candidateName, String jobTitle, String time,
            String location) {
        String subject = "Moi phong van cong viec: " + jobTitle;
        String content = String.format(
                "Chao %s,%n%nBan co lich phong van cho vi tri %s vao luc %s.%nDia diem/Link: %s%n%nChuc ban thanh cong!",
                candidateName,
                jobTitle,
                time,
                location
        );

        sendSimpleEmail(toEmail, subject, content);
    }

    public void sendApplicationStatusUpdate(String toEmail, String candidateName, String jobTitle, String status) {
        String subject = "Cap nhat trang thai ung tuyen: " + jobTitle;
        String content = String.format(
                "Chao %s,%n%nDon ung tuyen vi tri %s cua ban da duoc cap nhat trang thai: %s.%nVui long dang nhap vao he thong de xem chi tiet.",
                candidateName,
                jobTitle,
                status
        );

        sendSimpleEmail(toEmail, subject, content);
    }

    public void sendApplicationApprovedEmail(String toEmail, String candidateName, String jobTitle, String companyName) {
        String subject = "Chuc mung! Don ung tuyen cua ban da duoc duyet";
        String content = String.format(
                "Chao %s,%n%nChuc mung ban! Don ung tuyen cho vi tri %s tai %s da duoc duyet.%nNha tuyen dung se lien he voi ban de trao doi cac buoc tiep theo.%n%nTran trong,%nRecruitPro",
                candidateName,
                jobTitle,
                companyName
        );

        sendSimpleEmail(toEmail, subject, content);
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        String subject = "Dat lai mat khau RecruitPro";
        String content = String.format(
                "Chao ban,%n%nBan vua yeu cau dat lai mat khau RecruitPro.%nVui long mo link sau de tao mat khau moi trong vong 30 phut:%n%s%n%nNeu ban khong yeu cau, hay bo qua email nay.%n%nTran trong,%nRecruitPro",
                resetLink
        );

        sendSimpleEmail(toEmail, subject, content);
    }

    private void sendSimpleEmail(String toEmail, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (fromEmail != null && !fromEmail.isBlank()) {
            message.setFrom(fromEmail);
        }
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
        log.info("Sent email to {} with subject {}", toEmail, subject);
    }
}

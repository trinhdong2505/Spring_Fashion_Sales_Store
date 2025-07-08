package Spring_AdamStore.service;

public interface EmailService {

    void sendOtpRegisterEmail(String toEmail, String name, String otp);

    void sendPasswordResetCode(String toEmail, String name, String verificationCode);
}

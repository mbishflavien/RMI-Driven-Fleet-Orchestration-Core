package util;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

/**
 * EmailUtil - Sends OTP codes to users via SMTP.
 */
public class EmailUtil {

    // ─── CONFIGURE THESE ────────────────────────────────────────────────────
    private static final String SMTP_HOST       = "smtp.gmail.com";
    private static final String SMTP_PORT       = "587";
    private static final String SENDER_EMAIL    = "flavmbish@gmail.com";      // <-- change
    private static final String SENDER_PASSWORD = "YOUR_GMAIL_APP_PASSWORD";    // <-- change
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Sends a 6-digit OTP to the given recipient email address.
     *
     * @param recipientEmail the user's email address
     * @param otpCode        the 6-digit OTP string
     * @throws MessagingException if the email could not be sent
     */
    public static void sendOTP(String recipientEmail, String otpCode) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            SMTP_HOST);
        props.put("mail.smtp.port",            SMTP_PORT);
        props.put("mail.smtp.ssl.trust",       SMTP_HOST);

        javax.mail.Session session;
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(SENDER_EMAIL));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        msg.setSubject("Your OTP Verification Code");

        // Plain-text fallback
        String plain = "Your OTP code is: " + otpCode + "\n\n"
                     + "This code expires in 2 minutes.\n"
                     + "If you did not request this, please ignore this email.";

        // HTML body
        String html = "<div style='font-family:Segoe UI,Arial,sans-serif;max-width:480px;margin:0 auto;"
                    + "background:#f8fafc;border-radius:12px;overflow:hidden;border:1px solid #e2e8f0'>"
                    + "  <div style='background:linear-gradient(135deg,#0f172a,#1e3a5f);padding:32px;text-align:center'>"
                    + "    <div style='font-size:40px'>🔐</div>"
                    + "    <h1 style='color:#fff;margin:12px 0 4px;font-size:22px'>Email Verification</h1>"
                    + "    <p style='color:#94a3b8;margin:0;font-size:14px'>Vehicle Rental System</p>"
                    + "  </div>"
                    + "  <div style='padding:32px;text-align:center'>"
                    + "    <p style='color:#475569;font-size:15px;margin:0 0 24px'>Use the code below to verify your email address.</p>"
                    + "    <div style='background:#1e293b;border-radius:10px;padding:20px 40px;display:inline-block;margin:0 auto'>"
                    + "      <span style='font-size:36px;font-weight:700;letter-spacing:12px;color:#38bdf8;font-family:monospace'>"
                    + otpCode
                    + "      </span>"
                    + "    </div>"
                    + "    <p style='color:#94a3b8;font-size:13px;margin:20px 0 0'>⏱ This code expires in <strong>2 minutes</strong>.</p>"
                    + "    <p style='color:#cbd5e1;font-size:12px;margin:8px 0 0'>If you did not request this, you can safely ignore this email.</p>"
                    + "  </div>"
                    + "</div>";

        // Multipart: text + html
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(plain, "utf-8");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart("alternative");
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);

        msg.setContent(multipart);
        Transport.send(msg);

        System.out.println("[EmailUtil] OTP sent to " + recipientEmail);
    }
}

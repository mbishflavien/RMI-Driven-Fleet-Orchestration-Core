package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "otp_verification")
public class OTPVerification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int otpId;

    private String email;
    private String otpCode;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryTime;

    private boolean verified;

    public OTPVerification() {
    }

    public OTPVerification(int otpId, String email, String otpCode, Date expiryTime, boolean verified) {
        this.otpId = otpId;
        this.email = email;
        this.otpCode = otpCode;
        this.expiryTime = expiryTime;
        this.verified = verified;
    }

    public int getOtpId() {
        return otpId;
    }

    public void setOtpId(int otpId) {
        this.otpId = otpId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
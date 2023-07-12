package io.github.hossensyedriadh.openservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class PasswordResetRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -3472985581771177871L;

    private String username;

    private String otp;

    private String newPassword;
}

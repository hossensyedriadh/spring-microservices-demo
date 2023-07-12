package io.github.hossensyedriadh.openservice.entity;

import io.github.hossensyedriadh.openservice.enumerator.OtpType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("user_otps")
public final class Otp implements Serializable {
    @Serial
    private static final long serialVersionUID = 3760884549631164357L;

    @Setter(AccessLevel.NONE)
    @Id
    @Column("id")
    private Long id;

    @Column("otp")
    private String otp;

    @CreatedDate
    @Column("created_on")
    private Long createdOn;

    @Column("expires_on")
    private Long expiresOn;

    @Column("type")
    private OtpType type;

    @Column("for_user")
    private String forUser;
}

package io.github.hossensyedriadh.authservice.entity;

import io.github.hossensyedriadh.authservice.enumerator.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@Getter
@Setter
@Table("user_accounts")
public final class User {
    @Id
    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("authority")
    private Authority authority;

    @Column("email")
    private String email;
}

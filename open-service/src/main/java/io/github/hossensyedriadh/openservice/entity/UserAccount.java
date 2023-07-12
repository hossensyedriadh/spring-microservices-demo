package io.github.hossensyedriadh.openservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.hossensyedriadh.openservice.enumerator.Authority;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("user_accounts")
public final class UserAccount implements Serializable, Persistable<String> {
    @Serial
    private static final long serialVersionUID = -2556139220495827033L;

    @Id
    @Column("username")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column("password")
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column("authority")
    private Authority authority;

    @NotNull
    @Column("first_name")
    private String firstName;

    @NotNull
    @Column("last_name")
    private String lastName;

    @NotNull
    @Column("email")
    private String email;

    @NotNull
    @Column("address")
    private String address;

    @JsonIgnore
    @Transient
    private Boolean isNew = true;

    @Override
    public String getId() {
        return this.username;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}

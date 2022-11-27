package io.github.hossensyedriadh.authservice.model;

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
public final class AccessTokenRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6352438051443760783L;

    private String refresh_token;
}

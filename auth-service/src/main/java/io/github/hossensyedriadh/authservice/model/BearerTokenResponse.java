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
public final class BearerTokenResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 6606843263549579856L;

    private String access_token;

    private String access_token_type;

    private String refresh_token;
}

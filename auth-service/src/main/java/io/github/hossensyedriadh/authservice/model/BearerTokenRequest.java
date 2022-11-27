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
public final class BearerTokenRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5640191139939462589L;

    private String id;

    private String password;
}

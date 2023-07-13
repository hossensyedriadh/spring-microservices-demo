package io.github.hossensyedriadh.mailservice.model;

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
public final class MailModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -4892726687867338065L;

    private String from;

    private String to;

    private String subject;

    private String body;
}

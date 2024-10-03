package org.sandopla.photocenter.payload;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String password;
}

package org.sandopla.photocenter.payload;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

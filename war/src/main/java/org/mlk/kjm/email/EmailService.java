package org.mlk.kjm.email;

import static org.mlk.kjm.shared.ServletUtils.dateToString;

import org.mlk.kjm.users.AuthToken;

public class EmailService {
    public void sendAuthTokenEmail(AuthToken authToken) {
        System.out.println(authToken.getEmail());
        System.out.println(authToken.getCode());
        System.out.println(dateToString(authToken.getExpires()));
    }
}

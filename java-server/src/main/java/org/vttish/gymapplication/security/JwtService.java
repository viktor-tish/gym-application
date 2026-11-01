package org.vttish.gymapplication.security;

import org.vttish.gymapplication.security.model.AuthenticatedToken;
import org.vttish.gymapplication.security.model.UserPrincipal;

public interface JwtService {

  String generateToken(UserPrincipal principal);

  AuthenticatedToken parseToken(String token);
}

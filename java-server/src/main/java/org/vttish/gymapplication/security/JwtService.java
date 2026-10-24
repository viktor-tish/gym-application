package org.vttish.gymapplication.security;

public interface JwtService {
  String generateToken(UserPrincipal principal);

  UserPrincipal parseToken(String token);
}

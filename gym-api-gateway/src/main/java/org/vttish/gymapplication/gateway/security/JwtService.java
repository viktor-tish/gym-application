package org.vttish.gymapplication.gateway.security;

import org.vttish.gymapplication.gateway.security.model.ParsedJwt;
import org.vttish.gymapplication.gateway.security.model.UserPrincipal;

public interface JwtService {

  String generate(UserPrincipal principal);

  ParsedJwt parse(String token);
}

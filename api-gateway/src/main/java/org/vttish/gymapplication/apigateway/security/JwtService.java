package org.vttish.gymapplication.apigateway.security;

import org.vttish.gymapplication.apigateway.security.model.ParsedJwt;
import org.vttish.gymapplication.apigateway.security.model.UserPrincipal;

public interface JwtService {

  String generate(UserPrincipal principal);

  ParsedJwt parse(String token);
}

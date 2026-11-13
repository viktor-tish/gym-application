package org.vttish.gymapplication.security;

import org.vttish.gymapplication.security.model.ParsedToken;
import org.vttish.gymapplication.security.model.UserPrincipal;

public interface JwtService {

  String generate(UserPrincipal principal);

  ParsedToken parse(String token);
}

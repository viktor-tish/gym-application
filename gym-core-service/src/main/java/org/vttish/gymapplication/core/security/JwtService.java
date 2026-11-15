package org.vttish.gymapplication.core.security;

import org.vttish.gymapplication.core.security.model.ParsedToken;
import org.vttish.gymapplication.core.security.model.UserPrincipal;

public interface JwtService {

  String generate(UserPrincipal principal);

  ParsedToken parse(String token);
}

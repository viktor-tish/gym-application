package org.vttish.gymapplication.security;

import org.vttish.gymapplication.security.model.JwtMetadata;
import org.vttish.gymapplication.security.model.ParsedInternalJwt;
import org.vttish.gymapplication.security.model.UserPrincipal;
import org.vttish.gymapplication.security.properties.InternalJwtProperties;

public interface InternalJwtService {

  String generate(
      UserPrincipal principal,
      JwtMetadata metadata,
      InternalJwtProperties.Rule rule
  );

  ParsedInternalJwt parse(String token);
}

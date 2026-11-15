package org.vttish.gymapplication.core.security;

import org.vttish.gymapplication.core.security.model.JwtMetadata;
import org.vttish.gymapplication.core.security.model.ParsedInternalJwt;
import org.vttish.gymapplication.core.security.model.UserPrincipal;
import org.vttish.gymapplication.core.security.properties.InternalJwtProperties;

public interface InternalJwtService {

  String generate(
      UserPrincipal principal,
      JwtMetadata metadata,
      InternalJwtProperties.Rule rule
  );

  ParsedInternalJwt parse(String token);
}

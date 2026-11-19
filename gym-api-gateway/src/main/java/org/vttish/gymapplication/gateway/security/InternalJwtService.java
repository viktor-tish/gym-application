package org.vttish.gymapplication.gateway.security;

import org.vttish.gymapplication.gateway.security.model.JwtMetadata;
import org.vttish.gymapplication.gateway.security.model.ParsedInternalJwt;
import org.vttish.gymapplication.gateway.security.model.UserPrincipal;
import org.vttish.gymapplication.gateway.security.properties.InternalJwtProperties;

public interface InternalJwtService {

  String generate(
      UserPrincipal principal,
      JwtMetadata metadata,
      InternalJwtProperties.Rule rule
  );

  ParsedInternalJwt parse(String token);
}

package org.vttish.gymapplication.apigateway.security;

import org.vttish.gymapplication.apigateway.security.model.JwtMetadata;
import org.vttish.gymapplication.apigateway.security.model.ParsedInternalJwt;
import org.vttish.gymapplication.apigateway.security.model.UserPrincipal;
import org.vttish.gymapplication.apigateway.security.properties.InternalJwtProperties;

public interface InternalJwtService {

  String generate(
      UserPrincipal principal,
      JwtMetadata metadata,
      InternalJwtProperties.Rule rule
  );

  ParsedInternalJwt parse(String token);
}

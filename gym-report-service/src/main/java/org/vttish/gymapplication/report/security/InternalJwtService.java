package org.vttish.gymapplication.report.security;

import org.vttish.gymapplication.report.security.model.JwtMetadata;
import org.vttish.gymapplication.report.security.model.ParsedInternalJwt;
import org.vttish.gymapplication.report.security.model.UserPrincipal;
import org.vttish.gymapplication.report.security.properties.InternalJwtProperties;

public interface InternalJwtService {

  String generate(
      UserPrincipal principal,
      JwtMetadata metadata,
      InternalJwtProperties.Rule rule
  );

  ParsedInternalJwt parse(String token);
}

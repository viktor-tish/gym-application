package org.vttish.gymapplication.workloadreport.security;

import org.vttish.gymapplication.workloadreport.security.model.JwtMetadata;
import org.vttish.gymapplication.workloadreport.security.model.ParsedInternalJwt;
import org.vttish.gymapplication.workloadreport.security.model.UserPrincipal;
import org.vttish.gymapplication.workloadreport.security.properties.InternalJwtProperties;

public interface InternalJwtService {

  String generate(
      UserPrincipal principal,
      JwtMetadata metadata,
      InternalJwtProperties.Rule rule
  );

  ParsedInternalJwt parse(String token);
}

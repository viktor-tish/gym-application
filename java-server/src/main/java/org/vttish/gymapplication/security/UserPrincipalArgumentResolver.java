package org.vttish.gymapplication.security;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserPrincipalArgumentResolver implements HandlerMethodArgumentResolver {
  private static final String USER_PRINCIPAL_ATTRIBUTE = "userPrincipal";

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return UserPrincipal.class.equals(parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory
  ) {
    return webRequest.getAttribute(USER_PRINCIPAL_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
  }
}

package org.vttish.gymapplication.gateway.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymapplication.gateway.security.properties.InternalJwtProperties;
import org.vttish.gymapplication.gateway.security.properties.InternalJwtProperties.Rule;
import org.vttish.gymapplication.gateway.security.properties.InternalJwtProperties.Verb;

class InternalJwtScopePolicyTest {

  private InternalJwtScopePolicy scopePolicy;
  private Rule sampleRule;

  @BeforeEach
  void setUp() {
    sampleRule = new Rule(
        Set.of(Verb.GET, Verb.POST),
        "/api/v1/resource/**",
        List.of("audience-1"),
        List.of("scope:read", "scope:write")
    );

    InternalJwtProperties properties = new InternalJwtProperties(
        "test-secret-key-that-is-long-enough",
        3600000L,
        List.of(sampleRule)
    );

    scopePolicy = new InternalJwtScopePolicy(properties);
  }

  @Test
  void match_ShouldReturnRule_WhenMethodAndPathMatch() {
    Optional<Rule> result = scopePolicy.match("GET", "/api/v1/resource/123");

    assertTrue(result.isPresent());
    assertEquals(sampleRule, result.get());
  }

  @Test
  void match_ShouldReturnRule_WhenMethodIsLowercase() {
    Optional<Rule> result = scopePolicy.match("post", "/api/v1/resource/abc");

    assertTrue(result.isPresent());
    assertEquals(sampleRule, result.get());
  }

  @Test
  void match_ShouldReturnEmpty_WhenPathDoesNotMatch() {
    Optional<Rule> result = scopePolicy.match("GET", "/api/v2/other");

    assertTrue(result.isEmpty());
  }

  @Test
  void match_ShouldReturnEmpty_WhenMethodNotAllowed() {
    Optional<Rule> result = scopePolicy.match("DELETE", "/api/v1/resource/123");

    assertTrue(result.isEmpty());
  }

  @Test
  void match_ShouldReturnFirstMatchingRule_WhenMultipleRulesMatch() {
    Rule specificRule = new Rule(
        Set.of(Verb.GET),
        "/api/v1/resource/special",
        List.of("audience-2"),
        List.of("scope:special")
    );

    InternalJwtProperties properties = new InternalJwtProperties(
        "test-secret-key-that-is-long-enough",
        3600000L,
        List.of(specificRule, sampleRule)
    );

    InternalJwtScopePolicy multiRulePolicy = new InternalJwtScopePolicy(properties);

    Optional<Rule> result = multiRulePolicy.match("GET", "/api/v1/resource/special");

    assertTrue(result.isPresent());
    assertEquals(specificRule, result.get());
  }
}

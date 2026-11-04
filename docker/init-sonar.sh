#!/bin/sh
set -e

SONAR_URL=http://sonarqube:9000

echo "Waiting for SonarQube..."
until curl -s "$SONAR_URL/api/system/status" | grep -q '"status":"UP"'; do
  sleep 5
done

echo "Checking/updating administrative credentials..."
curl -s -u "admin:admin" -X POST \
  "$SONAR_URL/api/users/change_password" \
  --data-urlencode "login=admin" \
  --data-urlencode "previousPassword=admin" \
  --data-urlencode "password=$SONAR_PASSWORD" > /dev/null || \
  echo "Password already updated or using non-default credentials"

echo "Generating fresh analysis token..."
RESPONSE=$(curl -s -u "$SONAR_LOGIN:$SONAR_PASSWORD" -X POST \
  "$SONAR_URL/api/user_tokens/generate" \
  -d "name=ci-token-$(date +%s)")

TOKEN=$(echo "$RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "Error: Failed to generate token."
  echo "API Response was: $RESPONSE"
  exit 1
fi

mkdir -p /tmp/sonarqube-context
echo "$TOKEN" > /tmp/sonarqube-context/token.txt
echo "Token successfully generated and shared!"

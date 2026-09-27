#!/bin/sh
set -e

echo "Waiting for SonarQube to boot..."
until curl -s "http://sonarqube:9000/api/system/status" | grep -q '"status":"UP"'; do
  sleep 5
done
echo "SonarQube is ready!"

curl -s -u "admin:admin" -X POST \
  "http://sonarqube:9000/api/users/change_password" \
  --data-urlencode "login=admin" \
  --data-urlencode "previousPassword=admin" \
  --data-urlencode "password=$SONAR_PASSWORD" > /dev/null || true

TOKEN=$(curl -s -u "$SONAR_LOGIN:$SONAR_PASSWORD" -X POST \
  "http://sonarqube:9000/api/user_tokens/generate" \
  -d "name=ci-token-$(date +%s)" \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Running tests and SonarQube analysis..."
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url=http://sonarqube:9000 \
  -Dsonar.token="$TOKEN" \
  -Dsonar.projectKey="${SONAR_PROJECT_KEY}" \
  -Dsonar.projectName="${SONAR_PROJECT_NAME}" \
  -Dsonar.qualitygate.wait=true

echo "Done. SonarQube results → http://localhost:9000"

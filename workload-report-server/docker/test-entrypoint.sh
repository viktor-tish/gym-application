#!/bin/sh
set -e

SONAR_URL=http://sonarqube:9000

echo "Waiting for SonarQube..."
until curl -s "$SONAR_URL/api/system/status" | grep -q '"status":"UP"'; do
  sleep 5
done
echo "SonarQube is ready!"

TOKEN=$(cat /tmp/sonarqube-context/token.txt)

echo "Running tests and SonarQube analysis..."
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url="$SONAR_URL" \
  -Dsonar.token="$TOKEN" \
  -Dsonar.projectKey="${SONAR_PROJECT_KEY}" \
  -Dsonar.qualitygate.wait=true

echo "Done. SonarQube results -> http://localhost:9000"

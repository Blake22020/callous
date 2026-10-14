#!/bin/sh
# Gradle wrapper bootstrap.
# Пытается использовать gradle-wrapper.jar, иначе падает back на системный gradle (нужно для CI без бинарника).
APP_BASE=$(dirname "$0")
JAR="$APP_BASE/gradle/wrapper/gradle-wrapper.jar"
if [ -f "$JAR" ]; then
  exec java -jar "$JAR" "$@"
else
  echo "Wrapper JAR не найден, использую системный gradle..." >&2
  exec gradle "$@"
fi

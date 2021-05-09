#!/usr/bin/env sh

./gradlew tools:dev-cli:test tools:dev-cli:executable
cp ./tools/dev-cli/build/exec/dev-cli /usr/local/bin/
chmod +x /usr/local/bin/dev-cli

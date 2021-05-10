CALL gradlew tools:dev-cli:test tools:dev-cli:shadowJar
@echo off

set devCliDirectory=%homedrive%%homepath%\.dev-cli
echo %devCliDirectory%
if not exist %devCliDirectory% mkdir %devCliDirectory%
copy %cd%\tools\dev-cli\build\libs\dev-cli-all.jar %devCliDirectory%\dev-cli-all.jar
(
  echo @echo off
  echo java -jar %devCliDirectory%\dev-cli-all.jar %%^*
) > %devCliDirectory%\dev-cli.bat
echo 'dev-cli' installed. Please add '%devCliDirectory%' to your 'Path' variable.

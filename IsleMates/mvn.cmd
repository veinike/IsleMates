@echo off
setlocal
REM Workaround when "mvn" is not on PATH in CMD (e.g. old Cursor terminal session).
set "MAVEN_BIN=%USERPROFILE%\scoop\apps\maven\current\bin\mvn.cmd"
if exist "%MAVEN_BIN%" (
  "%MAVEN_BIN%" %*
  exit /b %ERRORLEVEL%
)
echo Maven not found at:
echo   %MAVEN_BIN%
echo Install: scoop install maven
echo Or add to PATH: %%USERPROFILE%%\scoop\apps\maven\current\bin
exit /b 1

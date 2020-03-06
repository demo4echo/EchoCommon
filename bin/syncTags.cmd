@ECHO OFF

SET NL=^&echo.

IF NOT "%1"=="" GOTO ADDV

PUSHD ../..
SET VAR=
FOR /F %%I IN ('git tag') DO CALL %0 %%I
SET VAR
GOTO END

:ADDV
SET VAR=%VAR%%NL%%1

:END
git tag -d %VAR%
git pull --tags
POPD

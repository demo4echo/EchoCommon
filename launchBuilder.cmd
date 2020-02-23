@ECHO OFF

REM Minikube formatr (needs to add some manipulation on the path:
REM If the path to mounted (local) is => C:\Users\tmeltse.COMVERSE\OneDrive - Mavenir Ltd\GitClones\GitHub\eclipse
REM Then the manipulated path would be => /c/Users/tmeltse.COMVERSE/OneDrive - Mavenir Ltd/GitClones/GitHub/eclipse (add "/" in the beginning ; replace "C" with "c" ; remove ":" ; "replace "\" with "/")
REM So the updated command would be => docker run -it --rm -v "/c/Users/tmeltse.COMVERSE/OneDrive - Mavenir Ltd/GitClones/GitHub/eclipse/echofe":$HOME/repo alpine ash

REM Docker Desktop format (no manipulation is needed), but the C drive needs to be shared once through the Docker Desktop GUI!
FOR %%I IN (.) DO SET CURRENT_FOLDER_NAME=%%~nxI
docker run -it --rm -v /var/run/docker.sock:/var/run/docker.sock -v "%USERPROFILE%\.gradle":/root/.gradle -v "%USERPROFILE%\.helm":/root/.helm -v "%CD%\..":/root/repo -w /root/repo -e MARK_OFF_CLUSTER_INVOCATION_ENV_VAR='dc' -e COMMON_SUB_MODULE_NAME_ENV_VAR="%CURRENT_FOLDER_NAME%" demo4echo/alpine_openjdk8_k8scdk:latest sh

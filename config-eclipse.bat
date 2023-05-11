@ECHO OFF

CD /D "%~dp0"


IF NOT EXIST .settings  MKDIR  .settings

COPY /Y  config-eclipse\build.gradle  .
COPY /Y  config-eclipse\settings.gradle  .
COPY /Y  config-eclipse\execute-sql.properties  scripts-sql\ant
COPY /Y  config-eclipse\org.eclipse.core.resources.prefs  .settings


IF EXIST .factorypath  DEL .factorypath

set TASK=eclipse

set CLASSPATH=..\~lib\gradle-wrapper\gradle-wrapper.jar
SET JVM_ARGS=-Xmx64m -Xms64m 
java %JVM_ARGS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %TASK%

ECHO. & PAUSE

@echo off

REM This script expects JAVA_HOME to point to the correct JDK 5.0 installation
REM A JDK 5.0 update 4 (JDK 1.5_04) or newer is needed for the profiler to work correctly
REM In case you need to customize it, please uncomment and modify the following line
REM set JAVA_HOME=C:\Software\jdk15_04

"%JAVA_HOME%\bin\java.exe" -agentpath:"%~dp0\..\lib\deployed\jdk15\windows-amd64\profilerinterface.dll"="\"%~dp0\..\lib\"",5140 %*

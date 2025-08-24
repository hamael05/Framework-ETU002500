@echo off
set "jar=jakarta.servlet-api-6.1.0-M2.jar"
set "jar2=gson-2.10.1.jar"

rem Compilation des fichiers Java
javac -d "src" -cp "%jar%;%jar2%" src/*.java

rem Création du fichier .jar
cd src
jar -cvf FrontController.jar servlet annotation mapping utility modelview exception session vm
pause

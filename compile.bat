@echo off
echo compiling the project
javac -d ./bin/ -cp ./bin/ ./src/simulator/*.java
echo.
echo Creating jar file
jar cvfe Simulator.jar bin.simulator.Simulator ./bin/simulator/*.class
pause

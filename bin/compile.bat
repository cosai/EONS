@echo off
echo compiling the project
javac -d ./ -cp ./ ../src/simulator/*.java
echo compilation done
echo.
echo Creating jar file
jar cvfe Simulator.jar simulator.Simulator ./simulator/*.class
echo  You can move this jar file anywhere to run
pause

@echo off
javac -d ./bin/ -cp ./bin/ ./src/simulator/*.java
jar cfm Simulator.jar manifest.mf  -C ./bin/simulator/ .

pause

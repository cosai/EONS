@echo off
:java -jar Simulator.jar trace_uni_cam.txt ttl prob #ofnodes #ofmessages #broadcasters alpha wantedprob isProphet lambda checkavg timelimit


echo Epidemic with TTS with TTS 2
java -jar Simulator.jar trace_uni_cam.txt 2 1.0 -1 100 -1 -1 -1 0 0 0 0

echo Random wouting with 10%
echo.
java -jar Simulator.jar trace_uni_cam.txt -1 0.1 -1 100 -1 -1 -1 0 0 0 0

echo Epidemic routing
echo.
java -jar Simulator.jar trace_uni_cam.txt -1 1.0 -1 100 -1 -1 -1 0 0 0 0
echo.


REM parameters stated in the article Probabilistic routing in intermittently connected networks
echo Prophet with prob Pinit 0.75 Alpha 0.98 Beta 0.25
echo.
java -jar Simulator.jar trace_uni_cam.txt -1 0.75 -1 100 -1 0.98 0.25 1 0 0 0

Echo Nodes Aware Density Based Routing
java -jar Simulator.jar trace_uni_cam.txt -1 1.0 -1 100 -1 -3 -3 0 2 0 0

Echo SCR Routing
java -jar Simulator.jar trace_uni_cam.txt -1 1.0 -1 100 -1 0.25 0.99 0 0.99 2 1350
:end


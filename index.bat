echo off
echo NUL>_.class&&del /s /f /q *.class
cls
javac index.java&&java index
start /min cmd /c "echo NUL>_.class&&del /s /f /q *.class"
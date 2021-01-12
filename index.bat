@echo off
cls
if exist build rmdir /s /q build
mkdir build
cd src
javac -d ../build com/krzem/launguage_prediction/Main.java&&jar cvmf ../manifest.mf ../build/launguage_prediction.jar -C ../build *&&goto run
cd ..
goto end
:run
cd ..
pushd "build"
for /D %%D in ("*") do (
	rd /S /Q "%%~D"
)
for %%F in ("*") do (
	if /I not "%%~nxF"=="launguage_prediction.jar" del "%%~F"
)
popd
cls
java -jar build/launguage_prediction.jar
:end

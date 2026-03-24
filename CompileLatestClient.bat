@echo off
title CompileLatestClient
call "%~dp0local-tools\\jdk-17.0.18+8\\bin\\java.exe" -Xmx8G -cp "buildtools/BuildTools.jar" net.lax1dude.eaglercraft.v1_8.buildtools.gui.CompileLatestClientGUI
del /S /Q "##TEAVM.TMP##\*"
rmdir /S /Q "##TEAVM.TMP##"
pause

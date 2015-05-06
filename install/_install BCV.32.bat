@echo off
assoc .class=BCV
assoc .apk=BCV
assoc .dex=BCV
ftype BCV="%CD%\BytecodeViewer.32.exe" "%%1"
echo.
echo.
echo Installed, .class, .apk and .dex will be associated with BytecodeViwer.32.exe
echo.
echo Note, if you move BytecodeViewer.32.exe
echo you'll need to re-run this program in the same directory as it.
echo.
echo.
pause
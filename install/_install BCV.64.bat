@echo off
assoc .class=BCV
assoc .apk=BCV
assoc .dex=BCV
ftype BCV="%CD%\BytecodeViewer.64.exe" "%%1"
echo.
echo.
echo Installed, .class, .apk and .dex will be associated with BytecodeViwer.64.exe
echo.
echo Note, if you move BytecodeViewer.64.exe
echo you'll need to re-run this program in the same directory as it.
echo.
echo.
pause
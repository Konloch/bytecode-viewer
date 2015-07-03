@echo off
assoc .class=BCV
assoc .apk=BCV
assoc .dex=BCV
ftype BCV="%CD%\BytecodeViewer.exe" "%%1"
echo.
echo.
echo Installed, .class, .apk and .dex will be associated with BytecodeViwer.exe
echo.
echo Note, if you move BytecodeViewer.exe
echo you'll need to re-run this program in the same directory as it.
echo.
echo.
pause
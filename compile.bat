@echo off
cls
echo Compiling...
javac *.java > a

echo Creating Stubs...
rmic TaskBag > a
rmic Master > a
rmic Worker > a

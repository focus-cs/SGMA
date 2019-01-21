set BATCH_PATH=D:/OneDrive/Documents/Professionnel/SGMA
set SCIFORMA_URL=https://socgen-maroc-dev.sciforma.net/sciforma/
set BATCH_MAIN=APISGMA.jar


set ROOT_DIR=%BATCH_PATH%
set LIB_DIR=%ROOT_DIR%/lib

cd %LIB_DIR%

IF EXIST "PSClient_en.jar" (
    del "PSClient_en.jar"
)
IF EXIST "PSClient.jar" (
    del "PSClient.jar"
)
IF EXIST "utilities.jar" (
    del "utilities.jar"
)

wget.exe  -O utilities.jar %SCIFORMA_URL%/utilities.jar
wget.exe  -O PSClient_en.jar %SCIFORMA_URL%/PSClient_en.jar
wget.exe -O PSClient.jar %SCIFORMA_URL%/PSClient.jar

cd %ROOT_DIR%

set JAVA_ARGS=-showversion
set JAVA_ARGS=%JAVA_ARGS% -Xms1024m
set JAVA_ARGS=%JAVA_ARGS% -Xmx2048m
set JAVA_ARGS=%JAVA_ARGS% -jar

set JAVA_PARAM=D:/OneDrive/Documents/Professionnel/SGMA/config/sciforma.properties

java %JAVA_ARGS% %BATCH_MAIN% %JAVA_PARAM%
 
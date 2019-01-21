BATCH_PATH="/pathsample/APISGMA"
BATCH_MAIN="APISGMA.jar"
SCIFORMA_URL="https://socgen-maroc-dev.sciforma.net/sciforma/"

ROOT_DIR=$BATCH_PATH
LIB_DIR=$ROOT_DIR/lib

cd $LIB_DIR

if test -f PSClient* ; then
    rm -f PSClient*
fi

if test -f utilities* ; then
    rm -f utilities*
fi

wget -O utilities.jar $SCIFORMA_URL/utilities.jar
wget -O PSClient_en.jar $SCIFORMA_URL/PSClient_en.jar
wget -O PSClient.jar $SCIFORMA_URL/PSClient.jar

cd $ROOT_DIR

JAVA_ARGS="-showversion"
JAVA_ARGS="$JAVA_ARGS -Xms1024m"
JAVA_ARGS="$JAVA_ARGS -Xmx2048m"
JAVA_ARGS="$JAVA_ARGS -jar"

JAVA_PARAM="/pathsample/APISGMA/config/sciforma.properties"

java $JAVA_ARGS $BATCH_MAIN $JAVA_PARAM
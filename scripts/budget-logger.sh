#! /bin/sh

JAVA_HOME=/usr/lib/jvm/default
PATH=$PATH:/usr/lib/jvm/default/bin

######## CONFIGURE ########
JAVA_PROGRAM_DIR="/usr/lib/jvm/default/bin/" # use full path to java bin dir, ex. "/usr/java/j2sdk1.4.2/bin/"
###########################

script=$0
count=0
while [ -L "$script" ]
do
  script=$(readlink "$script")
  count=`expr $count + 1`
  if [ $count -gt 100 ] 
  then
    echo "Too many symbolic links"
    exit 1
  fi
done

PROGRAM_DIR=`dirname ${script}`
PROGRAM_DIR=`cd "$PROGRAM_DIR"; pwd`

LIBDIR=${PROGRAM_DIR}/lib

cd "${LIBDIR}"
# build the classpath
for FILE in *.jar; do CLASSPATH="${CLASSPATH}:${LIBDIR}/${FILE}"; done

cd "${PROGRAM_DIR}"

${JAVA_PROGRAM_DIR}java -Xmx512m -splash:${PROGRAM_DIR}/data/splash.png -cp ${CLASSPATH} org.budget.logger.BudgetLogger ${PROGRAM_DIR}/webapp "/usr/bin/prism -webapp ./prism/app.webapp"

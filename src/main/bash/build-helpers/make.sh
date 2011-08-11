###
# #%L
# LaBoGrid
# %%
# Copyright (C) 2011 LaBoGrid Team
# %%
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the 
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public 
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/gpl-3.0.html>.
# #L%
###
#!/bin/bash

. ./versions.sh

if [[ -z DIMAWO_VERSION || -z LABOGRID_VERSION ]]
then
	echo "versions.sh does not contain proper definitions."
	exit 1
fi

ACTION=$1

DIMAWO_RELEASE_NAME="dimawo-"${DIMAWO_VERSION}
LABOGRID_RELEASE_NAME="labogrid-"${LABOGRID_VERSION}
DIMAWO_SRC=${DIMAWO_RELEASE_NAME}"-src.tar.gz"
LABOGRID_SRC=${LABOGRID_RELEASE_NAME}"-src.tar.gz"

BASE_DIR=`pwd`
MAVEN_CMD="mvn"
EXTRACT_CMD="tar --overwrite -xzvf"
BIN_DIR="bin/"
DOC_DIR="doc/"
JAVADOC_DIR="${DOC_DIR}/javadoc/"
LABOGRID_BIN_FILE="src/${LABOGRID_RELEASE_NAME}/target/site/dist/${LABOGRID_RELEASE_NAME}-jar-with-dependencies.jar"
LABOGRID_SCRIPTS_DIR="src/${LABOGRID_RELEASE_NAME}/src/main/bash/sim-helpers/"
SCHEMA_FILE="src/${LABOGRID_RELEASE_NAME}/src/main/schema/labogrid-conf-schema.xsd"
USERS_GUIDE_FILE="src/${LABOGRID_RELEASE_NAME}/target/latex/users-guide/users-guide.pdf"
DIMAWO_DOC_SRC="src/${DIMAWO_RELEASE_NAME}/target/doc/"
LABOGRID_DOC_SRC="src/${LABOGRID_RELEASE_NAME}/target/site/docs/${LABOGRID_VERSION}/doc/"

checkMavenAvailable()
{
	mvn -version > /dev/null 2>&1
	if [[ $? != 0 ]]
	then
		echo "You must install maven2 in order to be able to build sources using \
this script."
	exit 1
	fi
}

checkSourcesAvailable()
{
	if [[ ! (-f src/${DIMAWO_SRC} && -f src/${LABOGRID_SRC}) ]]
	then
		echo "Source archives not available:"
		echo src/${DIMAWO_SRC}
		echo src/${LABOGRID_SRC}
		exit 1
	fi
}

prepareSources()
{
	checkSourcesAvailable

	cd src
	${EXTRACT_CMD} ${DIMAWO_SRC}
	${EXTRACT_CMD} ${LABOGRID_SRC}
	cd ${BASE_DIR}
}

checkSources()
{
	if [[ ! (-d src/${DIMAWO_RELEASE_NAME} && -d src/${LABOGRID_RELEASE_NAME}) ]]
	then
		echo "Sources must be unpacked first."
		exit 1
	fi
}

deleteSources()
{
	rm -rf src/${DIMAWO_RELEASE_NAME}
	rm -rf src/${LABOGRID_RELEASE_NAME}
}

buildDiMaWoDoc()
{
	cd src/${DIMAWO_RELEASE_NAME}
	${MAVEN_CMD} javadoc:javadoc
	cd ${BASE_DIR}
}

buildLaBoGridDoc()
{
	cd src/${LABOGRID_RELEASE_NAME}
	${MAVEN_CMD} javadoc:javadoc
	cd ${BASE_DIR}
}

buildDiMaWoBin()
{
	cd src/${DIMAWO_RELEASE_NAME}
	${MAVEN_CMD} install
	cd ${BASE_DIR}
}

buildLaBoGridBin()
{
	cd src/${LABOGRID_RELEASE_NAME}
	${MAVEN_CMD} package
	cd ${BASE_DIR}
}

cleanDiMaWo()
{
	cd src/${DIMAWO_RELEASE_NAME}
	${MAVEN_CMD} clean
	cd ${BASE_DIR}
}

cleanLaBoGrid()
{
	cd src/${LABOGRID_RELEASE_NAME}
	${MAVEN_CMD} clean
	cd ${BASE_DIR}
}

checkBinFilesAvailable()
{
	if [[ ! (-f ${LABOGRID_BIN_FILE}) ]]
	then
		echo "LaBoGrid must be built (missing "${LABOGRID_BIN_FILE}")"
		exit 1
	fi

	if [[ ! (-f ${LABOGRID_BIN_FILE}) ]]
	then
		echo "Configuration XML Schema file is not available ("${SCHEMA_FILE}")"
		exit 1
	fi

	if [[ ! (-d ${LABOGRID_SCRIPTS_DIR}) ]]
	then
		echo "LaBoGrid scripts' directory does not exist ("${LABOGRID_SCRIPTS_DIR}")"
		exit 1
	fi
}

checkDocFilesAvailable()
{
	if [[ ! (-d ${DIMAWO_DOC_SRC} && -d ${LABOGRID_DOC_SRC}) ]]
	then
		echo "A doc. directory is missing."
		exit 1
	fi
}

installBin()
{
	checkBinFilesAvailable

	LIB_DIR=${BIN_DIR}/lib/
	mkdir -p ${LIB_DIR}

	cp ${LABOGRID_BIN_FILE} ${LIB_DIR}/labogrid.jar
	cp ${SCHEMA_FILE} ${LIB_DIR}/
	cp -r ${LABOGRID_SCRIPTS_DIR}/* ${BIN_DIR}/
	
	cd ${BIN_DIR}/
	chmod u+x setPermissions.sh
	./setPermissions.sh
	cd ${BASE_DIR}
}

installDoc()
{
	checkDocFilesAvailable

	mkdir -p ${JAVADOC_DIR}
	cp -r ${DIMAWO_DOC_SRC} ${JAVADOC_DIR}/dimawo
	cp -r ${LABOGRID_DOC_SRC} ${JAVADOC_DIR}/labogrid
	cp ${USERS_GUIDE_FILE} ${DOC_DIR}/
}

printUsage()
{
	echo "Usage: ./make.sh action"
	echo "Available actions: unpack, build-bin, build-doc, install-bin, \
install-doc, clean, build-install-bin, build-install-doc, build-install-all"
}

if [ $# -ne 1 ]
then
	printUsage
	exit 1
fi

case ${ACTION} in
	unpack)
	prepareSources
	;;
	build-bin)
	checkMavenAvailable
	checkSources
	buildDiMaWoBin
	buildLaBoGridBin
	;;
	build-doc)
	checkMavenAvailable
	checkSources
	buildDiMaWoDoc
	buildLaBoGridDoc
	;;
	install-bin)
	installBin
	;;
	install-doc)
	installDoc
	;;
	clean)
	checkMavenAvailable
	checkSources
	cleanDiMaWo
	cleanLaBoGrid
	;;
	build-install-bin)
	checkMavenAvailable
	checkSources
	buildDiMaWoBin
	buildLaBoGridBin
	installBin
	;;
	build-install-doc)
	checkMavenAvailable
	checkSources
	buildDiMaWoDoc
	buildLaBoGridDoc
	installDoc
	;;
	build-install-all)
	checkMavenAvailable
	checkSources
	buildDiMaWoBin
	buildLaBoGridBin
	installBin
	buildDiMaWoDoc
	buildLaBoGridDoc
	installDoc
	;;
	*)
	echo "Unknown action: "${ACTION}
	printUsage
	exit 1
esac


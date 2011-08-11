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

. ./config-cluster.sh
. ./config-labogrid.sh

LAUNCHER_CLASS="dimawo.exec.GenericBootstrapLauncher"
MEM_ARG=$(cat ~/LBGrid.jvm)

if [ -z MEM_ARG ];
then
	echo "Warning: no LBGrid.jvm file is available on this computer, using \
	default memory settings for JVM."
fi

if [[ ! (-f ${LABOGRID_JAR}) ]];
then
	echo "LaBoGrid is not properly installed."
	exit 1
fi

echo "parameters:"
PARAMS="${FACTORY_CLASS} ${WORK_PORT} ${WORK_DIR} \
	${MAX_NUM_CHILD} ${REL_THRESH} \
	${VERB_LEVEL} ${FACT_PARAMS}"
echo ${PARAMS}

mkdir -p ${WORK_DIR}
java ${MEM_ARG} -cp ${LABOGRID_JAR} ${LAUNCHER_CLASS} ${PARAMS}
	


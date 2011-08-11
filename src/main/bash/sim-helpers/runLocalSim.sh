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

# Runs simulations described in given configuration file locally.
# The given configuration file must properly reference the schema file.

. ./config-labogrid.sh

if [[ $# -ne 1 ]] ;
then
	echo "Usage: runLocalSim.sh {conf file}"
	exit 1
fi

# Run LaBoGrid locally is the same as considering a cluster of 1 computer
# running the bootstrap worker.
LAUNCHER_CLASS="dimawo.exec.GenericBootstrapLauncher"

# Memory usage is limited to 256 mega-bytes.
MEM_ARG="-Xmx256m"

# Working directory used by worker.
WORK_DIR="work_dir"

if [[ ! (-f lib/${LABOGRID_JAR}) ]];
then
	echo "LaBoGrid is not properly installed."
	exit 1
fi

mkdir ${WORK_DIR}
java ${MEM_ARG} -cp lib/${LABOGRID_JAR} ${LAUNCHER_CLASS} ${FACTORY_CLASS} \
50200 ${WORK_DIR} ${MAX_NUM_CHILD} ${REL_THRESH} ${VERB_LEVEL} $1

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
################################################################################
# Contains functions used by other scripts.
################################################################################

# Ignores "down" hosts instead of being locked by them.
SSH_OPTS="-o ConnectTimeOut=2"


# Deletes working directory on a given computer.
# Arg[1] The user name and host name or only the host name ([user@]host)
clearWorkDir()
{
	# If needed variable(s) is(are) not set, return with an error
	if [ -z WORK_DIR ] ;
	then
		echo "clearWorkDir: Mandatory variable WORK_DIR not set"
		return 1
	fi
	
	if [ $# -ne 1 ] ;
	then
		echo "Usage: cleanWorkDir [user@]host"
		return 2
	fi

	ssh ${SSH_OPTS} $1 "rm -rf "${WORK_DIR}
	return $?
}

# Downloads logs from worker computer and stores them into given directory.
# A directory named using the host name of worker computer is created. This
# directory contains the logs downloaded from corresponding computer.
# Arg[1] The user name and host name or only the host name ([user@]host)
# Arg[2] The directory logs' directory is created
downloadLogs()
{
	host=$1
	outDir=$2

	# If needed variable(s) is(are) not set, return with an error
	if [ -z WORK_DIR ] ;
	then
		echo "downloadLogs: Mandatory variable WORK_DIR not set"
		return 1
	fi
	
	if [ $# -ne 2 ] ;
	then
		echo "Usage: downloadLogs [user@]host outDir"
		return 2
	fi

	mkdir -p ${outDir}/${host}
	scp ${SSH_OPTS} ${host}:${WORK_DIR}/*.log ${outDir}/${host}/
	return $?
}

# Deletes input directory on a given computer.
# Arg[1] The user name and host name or only the host name ([user@]host)
clearInputDir()
{
	# If needed variable(s) is(are) not set, return with an error
	if [ -z INPUT_DIR ] ;
	then
		echo "clearInputDir: Mandatory variable INPUT_DIR not set"
		return 1
	fi
	
	if [ $# -ne 1 ] ;
	then
		echo "Usage: cleanInputDir [user@]host"
		return 2
	fi

	ssh ${SSH_OPTS} $1 "rm -rf "${INPUT_DIR}
	return $?
}

# Kills all java processes of given user on given computer. A side effect
# is that all LaBoGrid processes are also interrupted. If finer grain is needed,
# this functions must be enhanced.
# Arg[1] The user name and host name or only the host name ([user@]host)
killWorker()
{
	if [ $# -ne 1 ] ;
	then
		echo "Usage: killWorker [user@]host"
		return 2
	fi

	ssh ${SSH_OPTS} $1 'killall -u $USER java'
	return $?
}

# Starts the bootstrap worker on given computer.
# Arg[1] The user name and host name or only the host name ([user@]host)
# Arg[2] The directory logs are stored into
startBootstrapWorker()
{
	# If needed variable(s) is(are) not set, return with an error
	if [ -z INPUT_DIR ] ;
	then
		echo "startBootstrapWorker: Mandatory variable INPUT_DIR not set"
		return 1
	fi
	
	if [ $# -ne 2 ] ;
	then
		echo "Usage: startBootstrapWorker [user@]host log_dir"
		return 2
	fi

	ssh $1 "cd "${INPUT_DIR}"; sh runBootstrapWorker.sh" > \
		$2/$1.log 2>&1 &
	
	return $! # return the PID of bootstrap worker
}

# Starts a worker on given computer.
# Arg[1] The user name and host name or only the host name ([user@]host)
# Arg[2] The directory logs are stored into
startWorker()
{
	# If needed variable(s) is(are) not set, return with an error
	if [ -z INPUT_DIR ] ;
	then
		echo "startWorker: Mandatory variable INPUT_DIR not set"
		return 1
	fi
	
	if [ $# -ne 2 ] ;
	then
		echo "Usage: startWorker [user@]host log_dir"
		return 2
	fi

	ssh ${SSH_OPTS} $1 "cd "${INPUT_DIR}"; sh runWorker.sh" > \
		$2/$1.log 2>&1 &
}

# Creates input directory on given computer.
# Arg[1] The user name and host name or only the host name ([user@]host)
createInputDir()
{
	host=$1

	# If needed variable(s) is(are) not set, return with an error
	if [ -z INPUT_DIR ] ;
	then
		echo "createInputDir: Mandatory variable INPUT_DIR not set"
		return 1
	fi
	
	if [ $# -ne 1 ] ;
	then
		echo "Usage: createInputDir [user@]host"
		return 2
	fi

	ssh ${SSH_OPTS} ${host} "mkdir -p "${INPUT_DIR} > /dev/null
}

# Installs LaBoGrid and copies files needed to execute it on given computer.
# Arg[1] The user name and host name or only the host name ([user@]host)
installAndCopy()
{
	# If needed variable(s) is(are) not set, return with an error
	if [ -z INPUT_DIR ] ;
	then
		echo "installAndCopy: Mandatory variable INPUT_DIR not set"
		return 1
	fi
	
	if [ $# -ne 1 ] ;
	then
		echo "Usage: installAndCopy [user@]host"
		return 2
	fi

	host=$1
	createInputDir ${host}
	installLaBoGrid ${host}
	copyConfig ${host}
	copyUserFiles ${host}
}

# Installs LaBoGrid on given computer.
# Arg[1] The user name and host name or only the host name ([user@]host)
installLaBoGrid()
{
	if [ -z INPUT_DIR ] ;
	then
		echo "installLaBoGrid: Mandatory variable INPUT_DIR not set"
		return 1
	fi
	
	if [[ ! (-f lib/${LABOGRID_JAR} && -f lib/${LABOGRID_SCHEMA}) ]] ;
	then
		echo "installLaBoGrid: ${LABOGRID_JAR} and ${LABOGRID_SCHEMA} are\
		 missing in ./lib/ directory."
		return 3
	fi
	
	if [ $# -ne 1 ] ;
	then
		echo "Usage: installLaBoGrid [user@]host"
		return 2
	fi
	
	host=$1
	scp ${SSH_OPTS} lib/runBootstrapWorker.sh ${host}:${INPUT_DIR} > /dev/null
	scp ${SSH_OPTS} lib/runWorker.sh ${host}:${INPUT_DIR} > /dev/null
	scp ${SSH_OPTS} lib/${LABOGRID_JAR} ${host}:${INPUT_DIR} > /dev/null
	scp ${SSH_OPTS} lib/${LABOGRID_SCHEMA} ${host}:${INPUT_DIR} > /dev/null
}

# Copies LaBoGrid's configuration files on given computer, user files excluded.
# Arg[1] The user name and host name or only the host name ([user@]host)
copyConfig()
{
	# If needed variable(s) is(are) not set, return with an error
	if [ -z INPUT_DIR ] ;
	then
		echo "copyConfig: Mandatory variable INPUT_DIR not set"
		return 1
	fi
	
	if [ $# -ne 1 ] ;
	then
		echo "Usage: copyConfig [user@]host"
		return 2
	fi

	host=$1
	scp ${SSH_OPTS} config-cluster.sh ${host}:${INPUT_DIR} > /dev/null
	scp ${SSH_OPTS} config-labogrid.sh ${host}:${INPUT_DIR} > /dev/null
}

# Copies user files on given computer.
# Arg[1] The user name and host name or only the host name ([user@]host)
copyUserFiles()
{
	# If needed variable(s) is(are) not set, return with an error
	if [ -z INPUT_DIR ] ;
	then
		echo "copyUserFiles: Mandatory variable INPUT_DIR not set"
		return 1
	fi
	
	if [ $# -ne 1 ] ;
	then
		echo "Usage: copyUserFiles [user@]host"
		return 2
	fi

	host=$1
	if [ -n USER_FILES ] ;
	then
		scp ${SSH_OPTS} -r ${USER_FILES} ${host}:${INPUT_DIR} > /dev/null
	fi
}


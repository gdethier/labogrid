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

# Starts a stand alone server on this computer. This server may be used by
# workers to retrieve input files and/or store produced files.
#
# Arguments:
# Arg[1] The host name that should be used by workers to reach the server
# Arg[2] The port the server is listening to.
# Arg[3] The directory the server stores logs, output files, etc. into.

if [ $# -ne 3 ] ;
then
	echo "Usage: startStandAloneServer.sh {host} {port} {workDirPrefix}"
	exit 1
fi

. ./config-labogrid.sh

java -cp lib/${LABOGRID_JAR} laboGrid.standalone.StandAloneDistributedAgent \
$1 $2 $3


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

# Converts the output of LaBoGrid simulations into portable text files.
# There is one file per slice of the lattice. This way, the size of each file
# remains small regarding the overall size of the lattice.
# The content of these files can then be read for further post-processing.
#
# Parameters:
# Arg[1] Path to files produced by LaBoGrid. This is generally a collection of
# ".dat" files. One file must be "mGraph.dat".
# Arg[2] Prefix of a directory that will contain produced files. The string "_x"
# is appended to this prefix where x is considered simulation iteration.
# Arg[3] The type of produced slices. Possible values are XY, YZ and XZ. XY
# representing slices perpendicular to z-axis and so on.
# Arg[4] The considered iteration i.e. the final iteration of the simulation.

. ./config-labogrid.sh

if [[ $# -ne 4 ]] ;
then
	echo "Usage: convertOutput.sh {path to input} {path to output} \
{slice type} {iteration}"
	exit 1
fi

# The class containing main method.
CLASS="laboGrid.utils.ConvertIntoASCII"
# The number of slices produced at a time. The higher this parameter is,
# the faster output files are produced but at the cost of higher memory
# consumption.
SLICES_AT_A_TIME="20"
# Content type of files produced by LaBoGrid. By default, it's "compressed".
# Other possible values are "raw" and "mixed".
CONTENT_TYPE="compress"

java -cp lib/${LABOGRID_JAR} ${CLASS} $1 $2 $3 ${SLICES_AT_A_TIME} $4 ${CONTENT_TYPE}


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
# This file contains the parameters for the execution of LaBoGrid simulations.
################################################################################

# LaBoGrid's configuration
# Arg[1] The configuration file of the simulations to execute.
# Arg[2] (optional) a CCP file
FACT_PARAMS="conf.xml"

# Files to deploy on computers that will run simulations. The only required
# file is the configuration file. Other optional files may also be provided:
# a solid file, a CCP file, etc.
USER_FILES="conf.xml"

# Port number used by workers
WORK_PORT="50200"

# Stand alone server host name
SA_HOST="ail3"
# Stand alone server port
SA_PORT="50200"

# Master/Worker factory to use in order to instantiate LaBoGrid
FACTORY_CLASS="laboGrid.LaBoGridFactory"

# Jar file containing LaBoGrid binaries
LABOGRID_JAR="labogrid.jar"
# LaBoGrid's configuration file schema (the file must be correctly referenced
# in LaBoGrid XML configuration file).
LABOGRID_SCHEMA="labogrid-conf-schema.xsd"

# Verbosity level (the higher the level, the lower the amount of output)
VERB_LEVEL="0"

# MN-tree parameters
MAX_NUM_CHILD="2"
REL_THRESH="2"

# The directory that will contain the output of SSH processes running workers.
LOG_DIR="ssh-log/"

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
# This file contains the parameters describing the cluster to use in order
# to execute LaBoGrid simulations.
################################################################################

# Input directory for workers; will contain LaBoGrid library and scripts,
# as well user's input files.
INPUT_DIR="/tmp/labogrid/in/"

# Working directory of workers; will contain all temporary files produced by
# LaBoGrid during its execution.
WORK_DIR="/tmp/labogrid/work/"

# Bootstrap computer
BOOTSTRAP_COMP="candi01"

# Cluster (without bootstrap computer)
CLUSTER="\
candi02 \
candi03 \
candi05 \
"


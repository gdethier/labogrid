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

# Copies configuration and user files to cluster's computers.

. ./config-cluster.sh
. ./config-labogrid.sh
. ./common-lib.sh

echo "Replicate data on bootstrap computer "${BOOTSTRAP_COMP}
createInputDir ${BOOTSTRAP_COMP}
copyConfig ${BOOTSTRAP_COMP}
copyUserFiles ${BOOTSTRAP_COMP}

for i in ${CLUSTER}
do
	echo "Replicate data on "${i}
	createInputDir ${i}
	copyConfig ${i}
	copyUserFiles ${i}
done


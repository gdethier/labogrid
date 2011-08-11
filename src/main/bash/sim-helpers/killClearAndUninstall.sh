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

# Kills all LaBoGrid processes and removes input and working directories.
# This operation essentially cleans-up the cluster.

. ./config-labogrid.sh
. ./config-cluster.sh
. ./common-lib.sh

echo "Kill, clear and configure "${BOOTSTRAP_COMP}"."
killWorker ${BOOTSTRAP_COMP}
clearWorkDir ${BOOTSTRAP_COMP}
clearInputDir ${BOOTSTRAP_COMP}

for i in ${CLUSTER};
do
	echo "Kill, clear and configure "${i}"."
	killWorker ${i}
	clearWorkDir ${i}
	clearInputDir ${BOOTSTRAP_COMP}
done


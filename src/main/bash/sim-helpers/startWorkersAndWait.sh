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

# Starts a worker on each computer of the cluster and waits for all workers
# to terminate their execution.

. ./config-labogrid.sh
. ./config-cluster.sh
. ./common-lib.sh

# Create logs directory
mkdir -p ${LOG_DIR}

echo "Deploying bootstrap peer on "${BOOTSTRAP_COMP}
startBootstrapWorker ${BOOTSTRAP_COMP} ${LOG_DIR}
BOOT_PID=$?

# Give time to bootstrap to be up and running
sleep 2s

for i in ${CLUSTER};
do
	echo "Deploying peer on "${i}
	startWorker ${i} ${LOG_DIR}
done

echo "Waiting for the end of simulations..."
wait


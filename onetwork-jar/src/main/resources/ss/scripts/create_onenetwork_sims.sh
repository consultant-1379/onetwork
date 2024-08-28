#!/bin/bash

# Created by  : Fatih ONUR
# Created in  : 18.04.2011
##
### VERSION HISTORY
# Ver1        : Created for OneNetwork-imulation Service
# Purpose     : Creates simulations from ready build mo and mml files
# Description : Creates OneNetwork simulations
# Date        : 2015.09.20
# Who         : Fatih ONUR
##########################################


if [ "$#" -ne 0 ]
then
cat<<HELP

####################
# HELP
####################

Usage  : $0

Example: $0

DESCRP : Creates simulations from ready build mo and mml files
HELP

exit 1
fi

################################
# Assign common variables
################################
DATE=`date +%H%M%S`
PWD=`pwd`
NOW=`date +"%Y_%m_%d_%T:%N"`

ONE_NETWORK_DIR=/tmp/prod/onenetwork
ONE_NETWORK_LOG_DIR=/tmp/prod/onenetwork_log

#LOGFILE=`cd ../;pwd`"/$NOW.log"
#LOGFILE=$ONE_NETWORK_DIR/$NOW.log
LOGFILE=$ONE_NETWORK_LOG_DIR/$NOW.log

CREATE_PORTS_SCRIPT=create_onenetwork_sim_ports.pl

##############################
# Precheck functions
##############################

checkExistingSimulation() #Simname
{
 if [ -f "$HOME/netsimdir/$1.zip" ]; then
     # *** Note ***
     # Assuming simulations are stored in default dir $HOME/netsimdir
     echo "Simulation $HOME/netsimdir/$1.zip"
     echo "already exists. Delete it and run again."
     exit 2
 fi
}

setupOneNetworkDirs()
{
    #echo "++Removing content of the existing onenetwork folder"
    #rm -rfv $ONE_NETWORK_DIR 2> /dev/null
    mkdir -p $ONE_NETWORK_LOG_DIR
}

#
## catch control-c keyboard interrupts
#
control_c()
{
  echo -en "\n*** Ouch! Exiting ***\n"
  /bin/ps -eaf | grep "$0" | grep -v grep | awk '{print $2}' | xargs kill -9
  exit $?
}

################################
# Main program
################################

# trap keyboard interrupt (control-c)
trap control_c SIGINT

setupOneNetworkDirs

echo "$0: started `date`" | tee -a $LOGFILE


SCRIPT_LIST=`ls $ONE_NETWORK_DIR/*.mml 2> /dev/null | grep -iv ports`

echo "SCRIPTLIST= $SCRIPT_LIST"

( $ONE_NETWORK_DIR/$CREATE_PORTS_SCRIPT 2>&1 ) | tee -a $LOGFILE

for mmlScript in $SCRIPT_LIST
do
    dateFull=`date`
    echo "$dateFull - Running $mmlScript" | tee -a $LOGFILE
    /netsim/inst/netsim_pipe < $mmlScript | tee -a $LOGFILE
    SUCCESS=`tac $LOGFILE |egrep -m 1 .`
    if [[ $SUCCESS != "OK" ]]
	then
    	echo "ERROR:-$mmlScript: Something went wrong during the execution. See the log file: $LOGFILE"
		exit 201
	fi
done

echo ""| tee -a $LOGFILE
echo "$0: ended at `date`" | tee -a $LOGFILE
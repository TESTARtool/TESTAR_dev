#!/bin/bash
# sudo crontab -e
# @reboot /home/testar/runner.sh
# sudo ps -aux | grep runner.sh
mount -t cifs -o username=user,password=password //qsamba.testar.org/aaron/fernando /home/testar/qsamba
if [ $? -eq 0 ]; then
    echo mount OK
else
    echo mount FAIL check userpassword
	exit 1
fi

# 10.102.0.225 172.17.0.1
IP=`hostname -I | awk -F ' ' '{print $1}'`
echo "My IP address is : $IP"

mkdir -p /home/testar/qsamba/scheduler/${IP}/{queue,current,done}

cd /home/testar/qsamba/scheduler/${IP}/

{

RUNNERBASE=`pwd`

echo "#@   Runner Base dir : $RUNNERBASE"

echo "#@#  Starting Infinite loop `date`"

while :
do
	cd ${RUNNERBASE}/queue/
	pwd
	NEXTJOB=`ls -1 *.sh 2>/dev/null | sort |head -n 1| sed "s/\r//g" `

	if [ "$NEXTJOB" = "" ]; then
		echo "#@  No jobs in queue `date`"
		sleep 60
	else
	    CURRENTJOB=`date +%Y%m%d-%H%M`-$NEXTJOB
		mv -v $NEXTJOB ../current/$CURRENTJOB
		cd ../current/
		echo "#@# # Start step:  `date`"
		dos2unix $CURRENTJOB
		./$CURRENTJOB
		sleep 10
		echo "#@# # Stop step:  `date`"
		mv -v *.sh ../done/
		sleep 10
		break
	fi
done

#reboot
/sbin/reboot

}  2>&1  | tee -a runner.log
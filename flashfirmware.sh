
FIRM_PATH=$1
if [ -z $FIRM_PATH ]; then
 FIRM_PATH=/Volumes/build/out/target/product/ouya_1_1
fi

adb reboot bootloader
fastboot flash boot $FIRM_PATH/boot.img
fastboot flash system $FIRM_PATH/system.img
fastboot flash bootloader $FIRM_PATH/bootloader.bin
fastboot reboot-bootloader

sleep 0.5

fastboot flash recovery $FIRM_PATH/recovery.img
fastboot format cache
fastboot format userdata
fastboot reboot


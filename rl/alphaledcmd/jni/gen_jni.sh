#!/bin/sh
javah -o head_led.h -jni -classpath ../build/intermediates/classes/debug/ com.ubtrobot.ledcmds.jni.LedControl

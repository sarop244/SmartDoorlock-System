import RPi.GPIO as GPIO

import time

#도어락 작동
def Doorlock():
    
    GPIO.setmode(GPIO.BCM)
    
    GPIO.setup(18,GPIO.OUT)
    
    time.sleep(0.5)
    
    GPIO.output(18,False)
    
    print('open')
    
    time.sleep(2)
    
    GPIO.cleanup()
    
    print('close')
    
    time.sleep(0.5)
     

'''
Created on Dec 18, 2013

@author: chau
'''
import time
import cwiid
import socket

def sendMessage(msg):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('localhost', 8888))
    s.send(msg)
    s.close

def getStringFromID(ID):
    if ID == cwiid.BTN_DOWN:
        return "DOWN"
    
    if ID == cwiid.BTN_LEFT:
        return "LEFT"
    
    if ID == cwiid.BTN_UP:
        return "UP"
    
    if ID == cwiid.BTN_RIGHT:
        return "RIGHT"
    
    if ID == cwiid.BTN_1:
        return "1"
    
    if ID == cwiid.BTN_2:
        return "2"
    
    if ID == cwiid.BTN_A:
        return "A"
    
    if ID == cwiid.BTN_B:
        return "B"        
    
    if ID == cwiid.BTN_HOME:
        return "HOME"
    
    if ID == cwiid.BTN_PLUS:
        return "PLUS"
    
    if ID == cwiid.BTN_MINUS:
        return "MINUS"
    
    if ID == (cwiid.BTN_B + cwiid.BTN_UP):
        return "B + UP"
    
    if ID == (cwiid.BTN_B + cwiid.BTN_DOWN):
        return "B + DOWN"
    
    if ID == (cwiid.BTN_B + cwiid.BTN_LEFT):
        return "B + LEFT"
    
    if ID == (cwiid.BTN_B + cwiid.BTN_RIGHT):
        return "B + RIGHT"

def buttonPressed(ID):
    m = getStringFromID(ID)
    if m != None:
        sendMessage(m + " PRESSED\n")

def buttonReleased(ID):
    m = getStringFromID(ID)
    if m != None:
        sendMessage(m + " RELEASED\n")
        
def buttonCombined(ID):
    m = getStringFromID(ID)
    if m != None:
        sendMessage(m + "\n")

def control_robot():
    button_to_check = None
    is_looking_new = True
    is_combined = False
    thres = 0.1          # time to wait after a button was released
    
    print "Connecting to wiimote..."
    
    wd = cwiid.Wiimote();          
    print "connected to wiimote"    
    wd.rpt_mode  = cwiid.RPT_BTN
    
    while True:       
        button_id = wd.state['buttons']    
        if is_looking_new:
            if button_id != 0:
                button_to_check = button_id
                buttonPressed(button_id)
                is_looking_new = False
        else:
            if button_id == 0:
                if not is_combined:
                    buttonReleased(button_to_check)
                is_looking_new = True
                is_combined = False
                time.sleep(thres)
                    
            elif button_id != button_to_check:
                buttonCombined(button_id)
                is_combined = True
                
        time.sleep(0.01)      
            
# run the program
control_robot()


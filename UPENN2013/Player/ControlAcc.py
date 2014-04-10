'''
Created on Jan 15, 2014

@author: chau
'''
import time
import cwiid
import socket

wm = None

def sendMessage(msg):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('localhost', 8888))
    s.send(msg)
    s.close    

def normalizeX(val):
    retVal = (val-125)/25.0        
    
    if retVal < -1.0:
        retVal = -1.0             
    elif retVal > 1.0:
        retVal = 1.0                
        
    return retVal

def normalizeY(val):
    retVal = (val-125)/25.0*(-1)
    
    if retVal < -1.0:
        retVal = -1.0            
    elif retVal > 1.0:
        retVal = 1.0        
        
    return retVal

def normalizeXNun(val):
    retVal =  (val-123)/50.0
    
    if retVal < -1.0:
        retVal = -1.0            
    elif retVal > 1.0:
        retVal = 1.0        
        
    return retVal

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
    
    if ID == (cwiid.BTN_B + cwiid.BTN_1):
        return "B + 1"
    
    if ID == (cwiid.BTN_B + cwiid.BTN_2):
        return "B + 2"
    
    if ID == (cwiid.BTN_B + cwiid.BTN_A):
        return "B + A"
    
    if ID == (cwiid.BTN_B + cwiid.BTN_MINUS):
        return "B + MINUS"
    
    if ID == (cwiid.BTN_B + cwiid.BTN_PLUS):
        return "B + PLUS"
    
    if ID == (cwiid.BTN_B + cwiid.BTN_HOME):
        return "B + HOME"    
    
def getStringFromIDNun(ID):
    if ID == cwiid.NUNCHUK_BTN_C:
        return "C NUN"
    
    if ID == cwiid.NUNCHUK_BTN_Z:
        return "Z NUN"    

def buttonReleased(ID):
    m = getStringFromID(ID)
    if m != None:
        sendMessage(m + " RELEASED\n")        
        
def buttonCombined(ID):
    m = getStringFromID(ID)
    if m != None:
        sendMessage(m + "\n")    
    
def buttonPressed(ID):
    m = getStringFromID(ID)
    if m != None:
        sendMessage(m + " PRESSED\n")

def connectWiimote():
    print "Connecting..."
    global wm
    i = 1
    while not wm:
        try:
            wm = cwiid.Wiimote()
        except RuntimeError:
            if i>10:
                quit()
                break
            print "attempt " + str(i)
            i += 1
    wm.led = 1
    print "Connected"
    wm.rpt_mode = cwiid.RPT_BTN | cwiid.RPT_ACC | cwiid.RPT_NUNCHUK
    time.sleep(0.1)
        
def control_robot():
    # variables for Wiimote buttons
    button_to_check = None
    is_looking_new = True
    is_combined = False
    is_request_combined = True
    did_send_none = False
    
    # variables for nunchuk buttons and stick
    is_looking_new_nun = True
    button_to_check_nun = None
    head_thres = 20
    lastRecordedTime = 0
    did_send_none_nun = False
    
    while True:        
        # get the data
        state = wm.state
        acc_values = state['acc']
        nun_values = state['nunchuk']
        acc_nun_values = nun_values['acc']
        button_id = state['buttons']
        button_id_nun = nun_values['buttons']
        stick = nun_values['stick']
        x_stick = stick[0]
        y_stick = stick[1]
        
        # normalize values from acc
        x = acc_values[0]
        y = acc_values[1]
        x_normalized = normalizeX(x)
        y_normalized = normalizeY(y)        
        x_nun_normalized = normalizeXNun(acc_nun_values[0])        
        
        # for buttons of the Wiimote
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
                is_request_combined = True
                
            elif button_id != button_to_check:
                is_combined = True
                if is_request_combined:
                    buttonCombined(button_id)
                    is_request_combined = False            
            else:
                is_request_combined = True
                
        # for accel data of the Wiimote 
        if ((button_id == cwiid.BTN_B) or 
            (button_id == (cwiid.BTN_B + cwiid.BTN_1)) or
            (button_id == (cwiid.BTN_B + cwiid.BTN_2)) or
            (button_id == (cwiid.BTN_B + cwiid.BTN_A)) or
            (button_id == (cwiid.BTN_B + cwiid.BTN_DOWN)) or
            (button_id == (cwiid.BTN_B + cwiid.BTN_HOME)) or
            (button_id == (cwiid.BTN_B + cwiid.BTN_LEFT)) or
            (button_id == (cwiid.BTN_B + cwiid.BTN_MINUS)) or
            (button_id == (cwiid.BTN_B + cwiid.BTN_RIGHT)) or
            (button_id == (cwiid.BTN_B + cwiid.BTN_PLUS)) or
            (button_id == (cwiid.BTN_B + cwiid.BTN_UP))):        
             
            sendMessage("WIIMOTE " + str(x_normalized) + " " + str(y_normalized) + "\n")
            did_send_none = False
             
        else:
            if not did_send_none:
                sendMessage("WIIMOTE NONE\n")
                did_send_none = True
            
        # for button of the nunchuk
        if is_looking_new_nun and (button_id_nun != 0):
            sendMessage(getStringFromIDNun(button_id_nun) + " PRESSED\n")
            button_to_check_nun = button_id_nun
            is_looking_new_nun = False
            
        elif (not is_looking_new_nun) and button_id_nun == 0:
            sendMessage(getStringFromIDNun(button_to_check_nun) + " RELEASED\n")
            is_looking_new_nun = True
            
        # for head controller (nunchuk)
        if ((x_stick <= (55 + head_thres)) or 
            (x_stick >= (206 - head_thres)) or 
            (y_stick >= (205 - head_thres)) or 
            (y_stick <= (60 + head_thres))):
            
            currentTime = int(round(time.time() * 1000))
            
            if (currentTime - lastRecordedTime) > 300:
                if (x_stick <= (55 + head_thres)) and (y_stick >= (205 - head_thres)):
                    sendMessage("MOVE HEAD UP AND LEFT\n")
                    
                elif (x_stick >= (206 - head_thres)) and (y_stick >= (205 - head_thres)):
                    sendMessage("MOVE HEAD UP AND RIGHT\n")
                    
                elif (x_stick <= (55 + head_thres)) and (y_stick <= (60 + head_thres)):
                    sendMessage("MOVE HEAD DOWN AND LEFT\n")
                    
                elif (x_stick >= (206 - head_thres)) and (y_stick <= (60 + head_thres)):
                    sendMessage("MOVE HEAD DOWN AND RIGHT\n")
                    
                elif x_stick <= (55 + head_thres):
                    sendMessage("MOVE HEAD LEFT\n")
                    
                elif x_stick >= (206 - head_thres):
                    sendMessage("MOVE HEAD RIGHT\n")
                    
                elif y_stick >= (205 - head_thres):
                    sendMessage("MOVE HEAD UP\n")
                    
                elif y_stick <= (60 + head_thres):
                    sendMessage("MOVE HEAD DOWN\n")                                                   
        else:
            lastRecordedTime = 0
            
        # for acc in the nunchuk
        if button_id_nun == cwiid.NUNCHUK_BTN_Z:
            sendMessage("NUNCHUK " + str(x_nun_normalized) + "\n")
            did_send_none_nun = False
        else:
            if not did_send_none_nun:
                sendMessage("NUNCHUK NONE\n")
                did_send_none_nun = True
        
        time.sleep(0.15)
        
        
connectWiimote()
control_robot()

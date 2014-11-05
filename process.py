'''
 * 
 * This application is written by Mashnoor Lab, Bangladesh
 * Phone - +8801826636115
 * Facebook - facebook.com/Mashnoor
 * Email - nmmashnoor@gmail.com
 * 
 '''
#!/usr/bin/python

import cgitb
import os
import subprocess
import cgi
import uuid

'''
This Python Script is used for processing a wav file from user input!
'''

print "Content-Type: text/plain;charset=utf-8"
print


# Generate .wav file 

form = cgi.FieldStorage()
text = form.getvalue("mt")
sex = form.getvalue("sex")
id = str(uuid.uuid4())
if sex=="male":
	subprocess.Popen(['espeak', '-w', id + '.wav', text]) #See eSpeak documentation to figure out about the command
else:
	subprocess.Popen(['espeak','-v+f3', '-w', id + '.wav', text])

print id



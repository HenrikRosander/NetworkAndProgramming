# server.py

import socket


class Server:
    def __init__(self):
        self.host = '192.168.10.233'
        self.port = 1234
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    def bind(self):
        print(self.host, self.port)
        self.server.bind((self.host, self.port))
        self.server.listen(5)

    def accept(self):
        client, addr = self.server.accept()
        self.client = client
        self.addr = addr
        print('Got connection', self.addr)

    def send(self, data):
        self.client.send(data.encode('UTF-8'))

    def recv(self):  # recive data
        self.msg = self.client.recv(1024).decode('UTF-8')
        print(self.msg)


serv = Server()
serv.bind()
serv.accept()

run = True

while run:
    try:
        data = input('>>>')
        serv.send(data)

        serv.recv()
    except ConnectionResetError:
        print('Client lost ... trying to connect')
        serv.accept

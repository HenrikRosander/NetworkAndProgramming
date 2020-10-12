import socket


class Server:
    def __init__(self):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.host = '192.168.1.192'
        self.port = 1234
    def bind(self):
        self.server.bind((self.host, self.port))

    def accept(self):
        client, addr = self.server._accept()
        self.client = client
        self.addr = addr
        print('Got connection from ', self.addr)

    def send(self,data): #send data
        self.client.send(data.encode('UTF-8'))

    def recv(self): #recive data
        self.msg = self.client.recv(20400).decode('UTF-8')
        return self.msg



serv = Server()
serv.bind()
serv.accept()

#server.bind((host,port))
#server.listen(5)


run = True
#client, addr = server.accept()
#print('Got connection from', addr)

while run:
    try:
        client_response = serv.recv()
        data = input('>>>')
        serv.send(data) #send data to client
        #msg = client.recv(1024)
        print(serv.recv())
        
    except ConnectionResetError:
        print('Client lost, trying to reconnect')
        serv.accept()

#print('Got connection from ', addr)
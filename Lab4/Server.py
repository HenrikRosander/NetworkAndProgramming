import socket

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
host = '192.168.1.192'
port = 1234

server.bind((host,port))
server.listen(5)


run = True
client, addr = server.accept()
print('Got connection from', addr)
while run:
    try:
        data = input('>>>')
        client.send(data.encode('UTF-8')) #send data to client
        
        msg = client.recv(1024)
        
    except ConnectionResetError:
        print('Client lost, trying to reconnect')
        client, addr = server.accept()

print('Got connection from ', addr)
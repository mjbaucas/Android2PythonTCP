
import sys
import socket  
  
host_ip = sys.argv[1]  
port = int(sys.argv[2])

print('host ip: ', host_ip)# Should be displayed as: 127.0.1.1  

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  
s.bind((host_ip, port))  
s.listen(4)  
while True:  
    print('Hello')
    conn, addr = s.accept()  
    print('Connected by', addr)  
    data = conn.recv(2048)  
    if not data: 
        break  
    conn.sendall(data)# Send back the received data intact   
    print('Received', repr(data))  
    conn.close()
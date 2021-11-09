import sys
import socket
import time

counter = 0
average = 0.0
limit = 0

host_ip = sys.argv[1]
port = sys.argv[2]

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST, PORT))

while True:
    start = time.time()
    s.sendall(b'')
    data = s.recv(1024)
    end = time.time()
    average += end - start

    print('Recieved', repr(data))
    counter+=1
    if counter >= limit:
        print(average/limit)
        break


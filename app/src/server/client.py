import sys
import socket
import time
from utils import size_selector

total = 0.0
counter = 0

host_ip = sys.argv[1]
port = int(sys.argv[2])
data = size_selector(sys.argv[3])
limit = 120


timer = time.time()
current = time.time()
while current-timer < limit:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host_ip, port))
    s.sendall(str.encode(data))
    recieved = recvall(s, len(data))
    print('Recieved', repr(recieved))
    end = time.time()
    diff = end-current
    total += diff
    counter += 1
    current = end
    print(diff)
    #print(current-timer)

print("Total Time: " + str(total))
print("Total Packets: " + str(counter))
print("Average time: " + str(total/counter))

def recvall(sock, buff_size):
    data = b''
    while True:
        part = sock.recv(buff_size)
        data += part
        if len(part) < buff_size:
            # either 0 or end of data
            break
    return data
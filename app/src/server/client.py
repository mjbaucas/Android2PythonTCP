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
    
    start = time.time()
    s.sendall(str.encode(data))
    recieved = s.recv(2048)
    end = time.time()
    diff = end-start
    total += diff
    counter+=1

    print('Recieved', repr(recieved))
    current = time.time()
    print(diff)
    #print(current-timer)

print("Total Time: " + str(total))
print("Total Packets: " + str(counter))
print("Average time: " + str(total/counter))

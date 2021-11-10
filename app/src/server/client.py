import sys
import socket
import time
from utils import size_selector

average = 0.0

host_ip = sys.argv[1]
port = int(sys.argv[2])
data = size_selector(sys.argv[3])
limit = 60000



timer = time.time()
current = time.time()
while current-timer < limit:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host_ip, port))
    
    start = time.time()
    s.sendall(str.encode(data))
    recieved = s.recv(2048)
    end = time.time()
    average += end - start

    print('Recieved', repr(recieved))
    current = time.time()
    print(current-timer)

print("Average time: " + average)

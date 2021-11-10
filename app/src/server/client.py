import sys
import socket
import time
from utils import size_selector

average = 0.0

host_ip = sys.argv[1]
port = sys.argv[2]
data = size_selector(sys.argv[3])
limit = 60000

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((host_ip, port))

timer = time.time()
current = time.time()
while current-timer < limit:
    start = time.time()
    s.sendall(str.encode(data))
    data = s.recv(1024)
    end = time.time()
    average += end - start

    print('Recieved', repr(data))
    current = time.time()

print("Average time: " + average)

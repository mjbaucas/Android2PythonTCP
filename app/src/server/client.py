# size of block - in bytes
# 2 - 120
# 109 - 1080 
# 235 - 2200
# 456 - 4216
# 971 - 8800~
# 1788 - 16184
# 3650 - 33048

import sys
sys.path.append('../')
sys.path.append('../blockchain')

import socket
import time
from utils import size_selector, send_msg, recv_msg
from blockchain.public import Chain as PublicBlockChain

host_ip = sys.argv[1]
port = int(sys.argv[2])
data = size_selector(sys.argv[3])
chain = int(sys.argv[4])
size_of_block = int(sys.argv[5])
limit = 120

trusted_list = [
    "default",
]

# Public Key generated from an Secret Key of "0"
secret_key = "5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9"

for x in range(0, size_of_block):
    trusted_list.append("item" + str(x))

public_chain = PublicBlockChain(2)
public_chain.gen_next_block(secret_key, trusted_list)

transmission_time = []
process_time = []


for i in range(0,3):
    total = 0.0
    total_process = 0.0
    counter = 0

    timer = time.time()
    current = time.time()
    while current-timer < limit:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((host_ip, port))
        if chain == 1:
            new_data = data + "_" + public_chain.proof_of_work(public_chain.chain[1])
            start = time.time()
            send_msg(s, str.encode(new_data))
        else:
            start = time.time()
            send_msg(s, str.encode(data))
        recieved = recv_msg(s)
        end = time.time()
        diff_net = end-start
        diff = end-current
        current = end
        #if(len(recieved) == len(data)):
        #print(len(recieved))
        #print('Recieved' , repr(recieved))
        print(total_process)
        total += diff
        total_process += ((diff - diff_net) + (float(recieved)/1000))
        counter += 1
        #print(diff)
        #print(current-timer)

    print("Total Time: " + str(total))
    print("Total Packets: " + str(counter))
    print("Average time: " + str(total/counter))
    transmission_time.append(total/counter)
    print("Average Processor time: " + str(total_process/counter))
    process_time.append(total_process/counter)

print("Average Times: " + str(transmission_time))
print("Average Processor Times: " + str(process_time))


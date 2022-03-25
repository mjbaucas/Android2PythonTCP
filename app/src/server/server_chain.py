
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

import json
import datetime
import socket  
from utils import send_msg, recv_msg
from blockchain.private import Chain as PrivateBlockChain
from blockchain.public import Chain as PublicBlockChain

host_ip = sys.argv[1]  
port = int(sys.argv[2])
chain_length = int(sys.argv[3])
mode = int(sys.argv[4])
print('host ip: ', host_ip)# Should be displayed as: 127.0.1.1  

trusted_list = [
    "default",
]

# Public Key generated from an Secret Key of "0"
secret_key = "5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9"

for x in range(0, 456):
    trusted_list.append("item" + str(x))

public_chain = PublicBlockChain(3)
public_chain.gen_next_block(secret_key, trusted_list)

private_chain = PrivateBlockChain()
private_chain.gen_next_block(secret_key, trusted_list)

for x in range(0, chain_length):
    public_chain.gen_next_block("", [])
    private_chain.gen_next_block("", [])

#print(sys.getsizeof(private_chain.chain[1].transactions))
#print(sys.getsizeof(public_chain.chain[1].transactions))

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  
s.bind((host_ip, port))  
s.listen(10)  
total = 0
counter = 0

proof = public_chain.proof_of_work(public_chain.chain[1])

while True:  
    conn, addr = s.accept()  
    print('Connected by', addr)  
    data = recv_msg(conn)
    if not data: 
        break
    print(data)
    message = data.decode().split("_")
    print(message[0])
    
    delta = 0.0
    verified = False
    print(mode)
    if mode == 0:
        # For private verification, you need to provide the id of the transaction and the secret key of the block to access.
        start = datetime.datetime.now()
        found_block = private_chain.search_ledger(message[1]) # Pulls the 2nd block in the chain
        if found_block != None:
            if message[0] in found_block.transactions: 
                end = datetime.datetime.now()
                delta = int((end - start).total_seconds()*1000)
                total+= delta
                #print(delta)
                verified = True
                counter+=1
    elif mode == 1:
        # For public verification, the user will provide the block id and the proof of work to access it.
        start = datetime.datetime.now()
        found_block = public_chain.search_ledger(message[1]) # Pulls the block from the chain specified by the provided secret key
        if found_block != None:
            if (public_chain.verify_proof(found_block, proof)): # Simulates a successful verification assuming that the device will always provide the correct proof of work.
                end = datetime.datetime.now()
                delta = int((end - start).total_seconds()*1000)
                total+= delta
                #print(delta)
                verified = True
                counter+=1

    msg = "denied"
    if verified:
        msg = str(delta)
    send_msg(conn, bytearray(msg.encode())) # Confirm verification   
    print('Result:' + msg )
    print("Average Time: " + str(total/counter))  
    conn.close()


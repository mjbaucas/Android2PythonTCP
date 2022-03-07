
import sys
sys.path.append('../')
sys.path.append('../blockchain')

import socket  
from utils import send_msg, recv_msg
from blockchain.private import Chain as PrivateBlockChain
from blockchain.public import Chain as PublicBlockChain

trusted_list = [
    "default",
]

private_chain = PrivateBlockChain()
private_chain.gen_next_block("0", trusted_list)

public_chain = PublicBlockChain(4)
public_chain.gen_next_block("0", trusted_list)

for x in range(0,3659):
    private_chain.gen_next_block("0", [])
    public_chain.gen_next_block("0", [])

print(sys.getsizeof(private_chain.chain))
print(len(private_chain.chain))

proof = public_chain.proof_of_work(public_chain.chain[1])
print(public_chain.verify_proof(public_chain.chain[1], proof))

host_ip = sys.argv[1]  
port = int(sys.argv[2])
print('host ip: ', host_ip)# Should be displayed as: 127.0.1.1  

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  
s.bind((host_ip, port))  
s.listen(10)  
while True:  
    conn, addr = s.accept()  
    print('Connected by', addr)  
    data = recv_msg(conn)
    if not data: 
        break
    message = data.split("_")[0]
    print(message)
    
    verified = ""
    if(mode == 'private'):
        if(message in pchain.output_ledger()):
            verified = "YES"
        else:
            verified = "NO"
    elsif(mode == 'public'):
        proof = pchain.proof_of_work(public_chain.chain[1])
        if(pchain.verify_proof(public_chain.chain[1], proof)):
            verified = "YES"
        else:
            verified = "NO"
    send_msg(conn, bytearray(verified.encode())) # Confirm verification   
    print('Result', verified)  
    conn.close()

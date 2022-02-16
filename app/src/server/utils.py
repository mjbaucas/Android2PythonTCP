def gen_data(size):
    temp_string = ""
    for x in range(0, size):
        temp_string = temp_string + "x"
    return temp_string

def size_selector(option):
    return gen_data(int(option))
    
def recvall(sock, buff_size):
    data = b''
    while True:
        part = sock.recv(buff_size)
        data += part
        if len(part) < buff_size:
            # either 0 or end of data
            break
    return data
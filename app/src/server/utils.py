def gen_data(size):
    temp_string = ""
    for x in range(0, size):
        temp_string = temp_string + "x"
    return temp_string

def size_selector(option):
    if option == "1024":
        return gen_data(1024)
    elif option == "512":
        return gen_data(512)
    elif option == "256":
        return gen_data(256)
    else:
        return gen_data(128)
    

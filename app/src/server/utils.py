def gen_data(size):
    temp_string = ""
    for x in range(0, size):
        temp_string = temp_string + "x"
    return temp_string

def size_selector(option):
    return gen_data(int(option))
    

import os 

def delete():
    for ele in fnls:
        abs_path = os.path.join(os.getcwd(), 'data', 'out', ele)
        os.remove(abs_path)
        print(f"Deleted {abs_path}")

fnls = ['token.txt', 'parser_list.txt', 'new_symbol_table.txt', 'old_symbol_table.txt',
        'ir_emulate_result.txt', 'intermediate_code.txt']   
delete()
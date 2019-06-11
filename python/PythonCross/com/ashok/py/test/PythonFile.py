'''
Created on 10-Jun-2019

@author: ashok.kumar
'''







if __name__ == "__main__":
    try:
        f = open("/Users/ashok.kumar/github/workspace/PythonCross/test.txt")
        print(f.read())
        f.close()
        f = open("/Users/ashok.kumar/github/workspace/PythonCross/test.txt","r")
        for line in f:
            print(line) 
        f.close()
        
        #append
        f = open("/Users/ashok.kumar/github/workspace/PythonCross/test.txt","a")
        f.write("You have started education from ur village government school")
        f.write("\n")
        f.write("you have then gone to your maternal uncle place to study")
        f.close()
        
        #read
        f = open("/Users/ashok.kumar/github/workspace/PythonCross/test.txt","r")
        for line in f:
            print(line)
        f.close()
    except:
        print("An error occured")
    else:
        print("Nothing went wrong")
    finally:
        print("finally")
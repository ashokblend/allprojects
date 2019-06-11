'''
Created on 11-Jun-2019

@author: ashok.kumar
'''

class Person():
    '''
    classdocs
    '''
    print("hello my class")
    def __init__(self, fname, mname):
        '''
        Constructor
        '''
        self.fname = fname
        self.mname = mname
        print("inside init")
        
    if __name__ == "__main__":
        print("you are in class main")
     
    def getfname(self):
        return self.fname   

    def getmname(self):
        return self.mname

class Child(Person ):
    def __init__(self,fname,mname,lname):
        Person.__init__(self, fname, mname)
        self.lname=lname
    
    def getlname(self):
        return self.lname

if __name__ == "__main__":
    print("you are in file main")
    p = Person("ashok","kumar")
    print(p.getfname())
    c = Child("ashok","kumar","pandey")
    print(c.getlname())
    print(c.getfname())
    
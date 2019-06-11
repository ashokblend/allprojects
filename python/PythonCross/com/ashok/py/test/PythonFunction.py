'''
Created on 10-Jun-2019

@author: ashok.kumar
'''
def myfunction(fname):
    print(fname + " kumar")
    
def mycountry(country="India"):
    print("Country:"+ country)

def myfood(foods):
    for x in foods:
        print(x)

def mul(x, y):
    return x*y

x = lambda a: a+10
y = lambda a,b: a*b
print("function called:"+__name__)
if __name__ == '__main__':  #instead of == , is will not work: is checks if both operand referes to same object where == checks value
    print("hello main")
    myfunction("ashok")
    mycountry()
    mycountry("japan")
    foods=["Rice","whea","idly"]
    myfood(foods)
    print(mul(5,6))
    print(x(10))
    print(y(8,8))
    pass


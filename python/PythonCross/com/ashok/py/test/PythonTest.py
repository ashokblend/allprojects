'''
Created on 29-May-2019

@author: ashok.kumar
'''
from __builtin__ import str
print("hello ashok")
x ="ashok"
y  = "kumar"
z = x + y
print(z)
#Below will give runtime error as sum cannot happen on string and integer 
#a = x + 5 
x = 1 #int
y = 2.5 #float
z = 5j #complex
a = -1
b = 35656222554887711
print("x:"+str(type(x)) 
      +", y:"+str(type(y)) 
      +", z:"+str(type(z))
      +", a"+str(type(a))
      +", b:"+str(type(b)))
x = int(2)
y = int(2.8)
z = int("3")
print(x+y+z)
print(str(x)+str(y)+str(z))

##python string
a = "hello world"
print(a[0]) 
print(a[2:7])
a = " Hello ashok "
print("Strip:"+a.strip())
print("lenth:"+ str(len(a)))
print(a.split(" "))

#input data from user
print("Enter your name:")
#input name in quote
#x = input()
#print("Your name :"+x)
x = 10
y = x
print("x is y:"+str(x is y))
y=10
print("x is y:"+str(x is y))
x = "hello ashok"
print("h in x:"+str("h" in x))
print("z in x:"+str("z" in x))

#list
fruits = ["appple", "mango", "banana"]
print(fruits)
fruits[2]="coconut"
print(fruits[2])
for x in fruits:
    print(x)
if "mango" in fruits:
    print("Mango is present in fruits")
print("length of list:"+str(len(fruits)))
fruits.append("custard")
fruits.insert(1, "guava")
fruits.insert(2,"dummy")
fruits.append("dummy1");
for x in fruits:
    print(x)

fruits.remove("dummy")
fruits.pop()
print(fruits)
fruitsclone =fruits
print(fruitsclone)
del fruitsclone #it will delete the variable
print(fruits)
print("x"+x)
del x #it will delete variable x
#print("x:"+x)
fruits = list(("a","b"))
print(fruits)

#Tuple
tup = ("a",1,3.4)
for x in tup:
    print(x)
    
#tupe are unchangeable, cannot be removed or added
tup = tuple(("a",1,3.4))
print(tup)

##Set
thisset = {"a","b","c"}
print(thisset)
for x in thisset:
    print(x)

print("a" in thisset)
print("A" in thisset)
thisset.add("z")
print(thisset)
thisset.update(["e","f","g"])
print(thisset)
thisset.remove("g")
print(thisset)
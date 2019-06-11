'''
Created on 29-May-2019

@author: ashok.kumar
'''
#if elif else
b = 10
a=b
if a == 10 :
    print("a is 10")
elif a < 10 :
    print("a is less than or equal to 10")
else :
    print("a is greater then 10")
    
#print("a is 10") if a is 10 else print("no")

if a > b or a < b:
    print("test success")
    
#while loop

i = 1
while(i < 6):
    print(i)
    i +=1
    if i ==2:
        continue
    if i is 3:
        break
    i +=1

fruits = ["apple","mango","bannana","guava"]
for x in fruits:
    print(x)
    if(x == "bannana"): # we can also write like "x is "bannana""
        print("its bannana breaking the loop")
        break

##for loop with index
for i in range(len(fruits)):
    print(fruits[i])
#range
for x in range(10):
    print(x)

for x in range(2, 7):
    print(x)

#else in for loop
for x in range(6):
    print(x)
else :
    print("finally finished")
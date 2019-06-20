#drop table if exists employee
#CREATE TABLE employee (empid string,empname string,mobilename string,mobilecolor string,salary int) USING com.databricks.spark.csv OPTIONS (path "D:/github/FeatureTest/input/join/employee.csv", inferSchema "true")
#select empname,sum(salary) from employee group by empname
drop table if exists bigd
create table bigd(name string,salary decimal(10,5)) stored by 'carbondata'
load data inpath 'D:\github\FeatureTest\input\bigd.csv' into table bigd options('fileheader'='name,salary')
select * from bigd

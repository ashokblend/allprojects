#show tables
#drop table if exists temp
#CREATE TABLE temp (empname string,city string,salary bigint) USING com.databricks.spark.csv OPTIONS (path "/Users/ashok.kumar/github/workspace/spark/input/join/emp_salary.csv")
#select * from temp
set spark.sql.codegen.wholeStage=true
#drop table if exists emp_hive
#CREATE EXTERNAL TABLE IF NOT EXISTS emp_hive(empname string,salary int) partitioned by (city string) STORED AS textfile LOCATION '/Users/ashok.kumar/github/workspace/spark/store/ext/emp_hive'
#set hive.exec.dynamic.partition.mode=nonstrict
# insert into emp_hive select empname,city,salary from temp
#select * from emp_hive
#create table emp_orc stored as orc as select * from temp
#select * from emp_orc
#drop table temp
#drop table emp_orc
#drop table temp
#CREATE TABLE temp (mobileId string,mobileName string,mobileColor string, sales int) USING com.databricks.spark.csv OPTIONS (path "/Users/ashok.kumar/github/workspace/spark/input/join/mobile.csv")
#drop table if exists mobile_orc_native
#create table mobile_orc_native (mobileId string,mobileName string, mobileColor string, sales int) stored as ORC
#insert into mobile_orc_native select * from temp
#explain extended select * from mobile_orc_native
#SET spark.sql.orc.impl=native
#drop table if exists mobile_orc_hive
#create table mobile_orc_hive (mobileId string,mobileName string, mobileColor string, sales int) stored as ORC
#insert into mobile_orc_hive select * from temp
#explain extended select * from mobile_orc_hive
#select * from mobile_orc_hive
#select * from mobile_orc_native
#drop table if exists temp
#CREATE TABLE temp (imei string,protocol string,mac string, city string, sales int) USING com.databricks.spark.csv OPTIONS (path "/Users/ashok.kumar/github/workspace/spark/input/orc/20190411/")
#select * from temp
#SET spark.sql.orc.impl=hive
#SET spark.sql.orc.impl=native
#drop table if exists device_orc_native
#create table device_orc_native (imei string,protocol string,mac string, city string, sales int) stored as ORC
#insert into device_orc_native select * from temp
#drop table if exists device_orc_hive
#create table device_orc_hive (imei string,protocol string,mac string, city string, sales int) stored as ORC
#insert into device_orc_hive select * from temp
# select city,sum(sales) from device_orc_native group by city
#select city,sum(sales) from device_orc_hive group by city
#
#
#
#set spark.sql.codegen.wholeStage=false
#SET spark.sql.orc.enabled=false
# SET spark.sql.hive.convertMetastoreOrc=false
 SET spark.sql.orc.enabled=true
SET spark.sql.hive.convertMetastoreOrc=true
#drop table if exists device_orc
#create table device_orc (imei string,protocol string,mac string, sales int) PARTITIONED BY(city string) stored as ORC
#set hive.exec.dynamic.partition.mode=nonstrict
#insert into device_orc select imei,protocol,mac,sales,city from temp
#select * from device_orc
#insert into device_orc select * from temp
#insert into device_orc select * from temp
#insert into device_orc select * from temp
#insert into device_orc select * from temp
#select city,sum(sales) from device_orc group by city
#select * from device_orc
#drop table if exists device_orc_using
#create table device_orc_using (imei string,protocol string,mac string, sales int,city string) USING orc PARTITIONED BY(city)
#insert into device_orc_using select imei,protocol,mac,sales,city from temp
select * from device_orc_using
#create table hello (imei string,protocol string,mac string, sales int,city string) USING org.apache.spark.sql.execution.datasources.orc PARTITIONED BY(city)

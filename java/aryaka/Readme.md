Readme
1.Download code from github
2.Import aryaka project in eclipse https://github.com/ashokblend/allprojects/tree/master/java/aryaka
3.Run IPGenerator main programme by configuring "outDir" in main method. By default it will generate 10 million record
4.Run LoadData main programme by configuring below variable in main method
   dataDir: directory where files got generated at step3
   storePath: directory where this programme will write files
5. open command terminal and cd to <downloadedproject>/java/aryaka/ and run below command
    mvn clean install -DskipTests
6. Copy directory <downloadedproject>/java/aryaka/ to <tomcat>/webapps folder
7. open <tomcat>/webapps/aryaka/WEB-INF/web.xml in editor and configure paramname "storepath"
   change "storepath" value to storePath configured in step 4
8. start tomcat server
9. launch below url
      http://localhost:8080/aryaka/
      

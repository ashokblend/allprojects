It creates a structured store from given raw file. Each file in structured store will be sorted and compressed with snappy. Each file is created using LoadTask. LoadData spawns multiple loadtask, allocating raw files to it. This load task will sort the data and compresses it with snappy.

* Schema of sorted data
   * List<Chunk>: Chunk is block of rows. After writting block of rows in data file. We keep metadata like offset, length etc 
                  in meta file
                  * startIp
                  * endIp
                  * offset
                  * length of chunk
   * no of data file means, each file is created by parallel execution of LoadTask.

* Schema of meta
    * Meta file will have each load information
    * each load will have Chunk information
    * It will also have start and endIp
    
    
* To Search IP
   * Read metadata in memory
   * Search for Load information which will have given ip. Since each load will have range of ip in it. This helps in  
     filtering it out
   * After finding load, search for chunk which will have given ip. This will filter out lot of chunks.
   * Once it finds Chunk, it reads data from file using available offset and length of bytes.
 

    
* Steps to use this project
  * Download code from github
  * Import aryaka project in eclipse https://github.com/ashokblend/allprojects/tree/master/java/aryaka
  * Run IPGenerator main programme by configuring "outDir" in main method. By default it will generate 10 million record
  * Run LoadData main programme by configuring below variable in main method
     dataDir: directory where files got generated at step3
     storePath: directory where this programme will write files
  * open command terminal and cd to <downloadedproject>/java/aryaka/ and run below command
     mvn clean install -DskipTests
  * Copy directory <downloadedproject>/java/aryaka/ to <tomcat>/webapps folder
  * open <tomcat>/webapps/aryaka/WEB-INF/web.xml in editor and configure paramname "storepath"
    change "storepath" value to storePath configured in step 4
  * start tomcat server
  * launch below url
      http://localhost:8080/aryaka/
      


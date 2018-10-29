#!/bin/sh

tarPath=/Users/ashok.kumar/github/nifi/nifi/nifi-assembly/target/nifi-1.8.0-SNAPSHOT-bin.tar.gz
toolkitTar=/Users/ashok.kumar/github/nifi/nifi/nifi-toolkit/nifi-toolkit-assembly/target/nifi-toolkit-1.8.0-SNAPSHOT-bin.tar.gz
isCluster=true
isHttps=true
securestandalonepath=/Users/ashok.kumar/cluster/nifi-clusters/https/standalone/
secureclusterpath=/Users/ashok.kumar/cluster/nifi-clusters/https/cluster/
standalonepath=/Users/ashok.kumar/cluster/nifi-clusters/http/standalone/
clusterpath=/Users/ashok.kumar/cluster/nifi-clusters/http/cluster/

keystore=/Users/ashok.kumar/github/key/nifi/nifi_keystore.p12
truststore=/Users/ashok.kumar/github/key/nifi/nifi_truststore.p12
toolkitPath=/Users/ashok.kumar/cluster/nifi-clusters/nifi-toolkit

secure()
{
  if [ "$isCluster" == "true" ]; then
    securecluster
  else
    securestandalone
  fi
}

securecluster()
{
  echo "setting secure cluster"
  rm -rf ${secureclusterpath}
  file=$(basename "$tarPath")
  echo $secureclusterpath
  mkdir ${secureclusterpath}
  mkdir ${secureclusterpath}/nifi-1
  mkdir ${secureclusterpath}/nifi-2
  mkdir ${secureclusterpath}/nifi-3
  tar -C ${secureclusterpath}/nifi-1 -zxf $tarPath
  tar -C ${secureclusterpath}/nifi-2 -zxf $tarPath
  tar -C ${secureclusterpath}/nifi-3 -zxf $tarPath
  len=`expr ${#file} - 11`
  foldername=`echo $file|cut -c1-$len`
  mv ${secureclusterpath}/nifi-1/$foldername/* ${secureclusterpath}/nifi-1
  mv ${secureclusterpath}/nifi-2/$foldername/* ${secureclusterpath}/nifi-2
  mv ${secureclusterpath}/nifi-3/$foldername/* ${secureclusterpath}/nifi-3
  rmdir ${secureclusterpath}/nifi-1/$foldername
  rmdir ${secureclusterpath}/nifi-2/$foldername
  rmdir ${secureclusterpath}/nifi-3/$foldername

  ## configuring nifi-1
  nifi=${secureclusterpath}/nifi-1
  sed -i .bak -e 's/server.1=/#sever configuration/g' $nifi/conf/zookeeper.properties
  sed -i .bak -e 's/clientPort=2181/clientPort=3181/g' $nifi/conf/zookeeper.properties
  echo "server.1=localhost:3888:4888">>$nifi/conf/zookeeper.properties
  echo "server.2=localhost:3889:4889">>$nifi/conf/zookeeper.properties
  echo "server.3=localhost:3890:4890">>$nifi/conf/zookeeper.properties

  sed -i .bak -e 's/#java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000/java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6001/g' $nifi/conf/bootstrap.conf

  sed -i .bak -e 's/nifi.state.management.embedded.zookeeper.start=false/nifi.state.management.embedded.zookeeper.start=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.zookeeper.connect.string=/nifi.zookeeper.connect.string=localhost:3181,localhost:3182,localhost:3183/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.is.node=false/nifi.cluster.is.node=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.address=/nifi.cluster.node.address=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.protocol.port=/nifi.cluster.node.protocol.port=7997/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.host=/nifi.remote.input.host=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.socket.port=/nifi.remote.input.socket.port=6997/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.flow.election.max.candidates=/nifi.cluster.flow.election.max.candidates=1/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.protocol.is.secure=false/nifi.cluster.protocol.is.secure=true/g' $nifi/conf/nifi.properties



  sed -i .bak -e 's/nifi.security.keystore=/nifi.security.keystore=\/Users\/ashok.kumar\/cluster\/nifi-clusters\/key\/nifi\/nifi_keystore.p12/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keystoreType=/nifi.security.keystoreType=jks/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keystorePasswd=/nifi.security.keystorePasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keyPasswd=/nifi.security.keyPasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststore=/nifi.security.truststore=\/Users\/ashok.kumar\/cluster\/nifi-clusters\/key\/nifi\/nifi_truststore.p12/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststoreType=/nifi.security.truststoreType=jks/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststorePasswd=/nifi.security.truststorePasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.http.port=8080/nifi.web.http.port=/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.https.port=/nifi.web.https.port=9081/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.secure=false/nifi.remote.input.secure=true/g' $nifi/conf/nifi.properties


  mkdir -p $nifi/state/zookeeper
  echo "1">$nifi/state/zookeeper/myid
  mkdir -p $nifi/state/zookeeper/version-2

  sed -i .bak -e 's/<property name="Initial User Identity 1"><\/property>/<property name="Initial User Identity 1">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="Initial Admin Identity"><\/property>/<property name="Initial Admin Identity">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="Node Identity 1"><\/property>/<property name="Node Identity 1">UID=ashok-nifi, OU=nifi, CN=localhost<\/property><property name="Node Identity 2">UID=ashok-nifi, OU=nifi, CN=localhost<\/property><property name="Node Identity 3">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  #With above configuration you will be able to login to nifi ui using certificate but to get access, create policy by clicking access policy in root canvase

  ## ldap setup authorizers.xml
  ldap_setup $nifi

  ##configuration nifi-2
  nifi=${secureclusterpath}/nifi-2
  sed -i .bak -e 's/server.1=/#sever configuration/g' $nifi/conf/zookeeper.properties
  sed -i .bak -e 's/clientPort=2181/clientPort=3182/g' $nifi/conf/zookeeper.properties
  echo "server.1=localhost:3888:4888">>$nifi/conf/zookeeper.properties
  echo "server.2=localhost:3889:4889">>$nifi/conf/zookeeper.properties
  echo "server.3=localhost:3890:4890">>$nifi/conf/zookeeper.properties

  sed -i .bak -e 's/#java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000/java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6002/g' $nifi/conf/bootstrap.conf

  sed -i .bak -e 's/nifi.state.management.embedded.zookeeper.start=false/nifi.state.management.embedded.zookeeper.start=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.zookeeper.connect.string=/nifi.zookeeper.connect.string=localhost:3181,localhost:3182,localhost:3183/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.is.node=false/nifi.cluster.is.node=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.address=/nifi.cluster.node.address=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.protocol.port=/nifi.cluster.node.protocol.port=7998/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.host=/nifi.remote.input.host=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.socket.port=/nifi.remote.input.socket.port=6998/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.flow.election.max.candidates=/nifi.cluster.flow.election.max.candidates=1/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.protocol.is.secure=false/nifi.cluster.protocol.is.secure=true/g' $nifi/conf/nifi.properties

  sed -i .bak -e 's/nifi.security.keystore=/nifi.security.keystore=\/Users\/ashok.kumar\/cluster\/nifi-clusters\/key\/nifi\/nifi_keystore.p12/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keystoreType=/nifi.security.keystoreType=jks/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keystorePasswd=/nifi.security.keystorePasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keyPasswd=/nifi.security.keyPasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststore=/nifi.security.truststore=\/Users\/ashok.kumar\/cluster\/nifi-clusters\/key\/nifi\/nifi_truststore.p12/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststoreType=/nifi.security.truststoreType=jks/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststorePasswd=/nifi.security.truststorePasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.http.port=8080/nifi.web.http.port=/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.https.port=/nifi.web.https.port=9082/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.secure=false/nifi.remote.input.secure=true/g' $nifi/conf/nifi.properties
  mkdir -p $nifi/state/zookeeper
  echo "2">$nifi/state/zookeeper/myid
  mkdir -p $nifi/state/zookeeper/version-2

  sed -i .bak -e 's/<property name="Initial User Identity 1"><\/property>/<property name="Initial User Identity 1">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="Initial Admin Identity"><\/property>/<property name="Initial Admin Identity">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="Node Identity 1"><\/property>/<property name="Node Identity 1">UID=ashok-nifi, OU=nifi, CN=localhost<\/property><property name="Node Identity 2">UID=ashok-nifi, OU=nifi, CN=localhost<\/property><property name="Node Identity 3">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  #With above configuration you will be able to login to nifi ui using certificate but to get access, create policy by clicking access policy in root canvase

  ## ldap setup authorizers.xml
  ldap_setup $nifi

  ##configuration nifi-3
  nifi=${secureclusterpath}/nifi-3
  sed -i .bak -e 's/server.1=/#sever configuration/g' $nifi/conf/zookeeper.properties
  sed -i .bak -e 's/clientPort=2181/clientPort=3183/g' $nifi/conf/zookeeper.properties
  echo "server.1=localhost:3888:4888">>$nifi/conf/zookeeper.properties
  echo "server.2=localhost:3889:4889">>$nifi/conf/zookeeper.properties
  echo "server.3=localhost:3890:4890">>$nifi/conf/zookeeper.properties

  sed -i .bak -e 's/#java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000/java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6003/g' $nifi/conf/bootstrap.conf

  sed -i .bak -e 's/nifi.state.management.embedded.zookeeper.start=false/nifi.state.management.embedded.zookeeper.start=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.zookeeper.connect.string=/nifi.zookeeper.connect.string=localhost:3181,localhost:3182,localhost:3183/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.is.node=false/nifi.cluster.is.node=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.address=/nifi.cluster.node.address=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.protocol.port=/nifi.cluster.node.protocol.port=7999/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.host=/nifi.remote.input.host=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.socket.port=/nifi.remote.input.socket.port=6999/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.flow.election.max.candidates=/nifi.cluster.flow.election.max.candidates=1/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.protocol.is.secure=false/nifi.cluster.protocol.is.secure=true/g' $nifi/conf/nifi.properties

  sed -i .bak -e 's/nifi.security.keystore=/nifi.security.keystore=\/Users\/ashok.kumar\/cluster\/nifi-clusters\/key\/nifi\/nifi_keystore.p12/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keystoreType=/nifi.security.keystoreType=jks/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keystorePasswd=/nifi.security.keystorePasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keyPasswd=/nifi.security.keyPasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststore=/nifi.security.truststore=\/Users\/ashok.kumar\/cluster\/nifi-clusters\/key\/nifi\/nifi_truststore.p12/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststoreType=/nifi.security.truststoreType=jks/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststorePasswd=/nifi.security.truststorePasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.http.port=8080/nifi.web.http.port=/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.https.port=/nifi.web.https.port=9083/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.secure=false/nifi.remote.input.secure=true/g' $nifi/conf/nifi.properties
  mkdir -p $nifi/state/zookeeper
  echo "3">$nifi/state/zookeeper/myid
  mkdir -p $nifi/state/zookeeper/version-2

  sed -i .bak -e 's/<property name="Initial User Identity 1"><\/property>/<property name="Initial User Identity 1">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="Initial Admin Identity"><\/property>/<property name="Initial Admin Identity">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="Node Identity 1"><\/property>/<property name="Node Identity 1">UID=ashok-nifi, OU=nifi, CN=localhost<\/property><property name="Node Identity 2">UID=ashok-nifi, OU=nifi, CN=localhost<\/property><property name="Node Identity 3">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  #With above configuration you will be able to login to nifi ui using certificate but to get access, create policy by clicking access policy in root canvase

  ## ldap setup authorizers.xml
  ldap_setup $nifi

  #restart script
  echo "#!/bin/sh" >>${secureclusterpath}/restart-cluster.sh
  echo "cd ${secureclusterpath}nifi-1/bin/">>${secureclusterpath}/restart-cluster.sh
  echo "./nifi.sh restart">>${secureclusterpath}/restart-cluster.sh
  echo "cd ${secureclusterpath}nifi-2/bin/">>${secureclusterpath}/restart-cluster.sh
  echo "./nifi.sh restart">>${secureclusterpath}/restart-cluster.sh
  echo "cd ${secureclusterpath}nifi-3/bin/">>${secureclusterpath}/restart-cluster.sh
  echo "./nifi.sh restart">>${secureclusterpath}/restart-cluster.sh

  ## stop script
  echo "#!/bin/sh" >>${secureclusterpath}/stop-cluster.sh
  echo "cd ${secureclusterpath}nifi-1/bin/">>${secureclusterpath}/stop-cluster.sh
  echo "./nifi.sh stop">>${secureclusterpath}/stop-cluster.sh
  echo "cd ${secureclusterpath}nifi-2/bin/">>${secureclusterpath}/stop-cluster.sh
  echo "./nifi.sh stop">>${secureclusterpath}/stop-cluster.sh
  echo "cd ${secureclusterpath}nifi-3/bin/">>${secureclusterpath}/stop-cluster.sh
  echo "./nifi.sh stop">>${secureclusterpath}/stop-cluster.sh

  chmod -R 777 ${secureclusterpath}
}

securestandalone()
{
    echo "setting secure standalone"
    rm -rf ${securestandalonepath}
    mkdir ${securestandalonepath}
    file=$(basename "$tarPath")
    tar -C $securestandalonepath/ -zxf $tarPath
    len=`expr ${#file} - 11`
    foldername=`echo $file|cut -c1-$len`
    mv $securestandalonepath/$foldername/* $securestandalonepath
    rmdir $securestandalonepath/$foldername
    nifi=$securestandalonepath
    ##configuring nifi.properties
    sed -i .bak -e 's/nifi.security.keystore=/nifi.security.keystore=\/Users\/ashok.kumar\/github\/key\/nifi\/nifi_keystore.p12/g' $nifi/conf/nifi.properties
    sed -i .bak -e 's/nifi.security.keystoreType=/nifi.security.keystoreType=jks/g' $nifi/conf/nifi.properties
    sed -i .bak -e 's/nifi.security.keystorePasswd=/nifi.security.keystorePasswd=Myself@1986/g' $nifi/conf/nifi.properties
    sed -i .bak -e 's/nifi.security.keyPasswd=/nifi.security.keyPasswd=Myself@1986/g' $nifi/conf/nifi.properties
    sed -i .bak -e 's/nifi.security.truststore=/nifi.security.truststore=\/Users\/ashok.kumar\/github\/key\/nifi\/nifi_truststore.p12/g' $nifi/conf/nifi.properties
    sed -i .bak -e 's/nifi.security.truststoreType=/nifi.security.truststoreType=jks/g' $nifi/conf/nifi.properties
    sed -i .bak -e 's/nifi.security.truststorePasswd=/nifi.security.truststorePasswd=Myself@1986/g' $nifi/conf/nifi.properties
    sed -i .bak -e 's/nifi.web.http.port=8080/nifi.web.http.port=/g' $nifi/conf/nifi.properties
    sed -i .bak -e 's/nifi.web.https.port=/nifi.web.https.port=9084/g' $nifi/conf/nifi.properties
    sed -i .bak -e 's/nifi.remote.input.secure=false/nifi.remote.input.secure=true/g' $nifi/conf/nifi.properties

    sed -i .bak -e 's/#java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000/java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6004/g' $nifi/conf/bootstrap.conf

    ##configure authorization xml
    sed -i .bak -e 's/<property name="Initial User Identity 1"><\/property>/<property name="Initial User Identity 1">UID=ashok, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
    sed -i .bak -e 's/<property name="Initial Admin Identity"><\/property>/<property name="Initial Admin Identity">UID=ashok, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
    #With above configuration you will be able to login to nifi ui using certificate but to get access, create policy by clicking access policy in root canvase

    ## ldap setup authorizers.xml
    ldap_setup $nifi

    cd $toolkitPath/bin
    ./encrypt-config.sh -b ${securestandalonepath}/conf/bootstrap.conf  -k 0123456789ABCDEFFEDCBA98765432100123456789ABCDEFFEDCBA9876543210 -n ${securestandalonepath}/conf/nifi.properties

}
ldap_setup()
{
  echo "Setting ldap for $1"
  nifi=$1
  ## ldap setup authorizers.xml
  sed -i .bak -e 's/<property name="Authentication Strategy">START_TLS/<property name="Authentication Strategy">SIMPLE/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="Manager DN">/<property name="Manager DN">uid=admin,ou=system/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="Manager Password">/<property name="Manager Password">secret/g' $nifi/conf/authorizers.xml

  sed -i .bak -e 's/ldap-user-group-provider remove 2 lines. This is 1 of 2./ldap-user-group-provider remove 2 lines. This is 1 of 2. -->/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/To enable the ldap-user-group-provider remove 2 lines. This is 2 of 2./<!-- To enable the ldap-user-group-provider remove 2 lines. This is 2 of 2./g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="Url">/<property name="Url">ldap:\/\/localhost:10389/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="User Search Base">/<property name="User Search Base">ou=nifi,dc=horton/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/composite-user-group-provider remove 2 lines. This is 1 of 2./composite-user-group-provider remove 2 lines. This is 1 of 2. -->/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/To enable the composite-user-group-provider remove 2 lines. This is 2 of 2./<!-- To enable the composite-user-group-provider remove 2 lines. This is 2 of 2./g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="User Group Provider 1"><\/property>/<property name="User Group Provider 1">file-user-group-provider<\/property><property name="User Group Provider 2">ldap-user-group-provider<\/property>/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="User Group Provider">file-user-group-provider/<property name="User Group Provider">composite-user-group-provider/g' $nifi/conf/authorizers.xml

}
nonsecure()
{
  if [ "$isCluster" == "true" ]; then
    cluster
  else
    standalone
  fi
}

standalone()
{
   echo "setting non secure cluster"
   rm -rf ${standalonepath}
   mkdir ${standalonepath}
   file=$(basename "$tarPath")
   tar -C $standalonepath/ -zxf $tarPath
   len=`expr ${#file} - 11`
   foldername=`echo $file|cut -c1-$len`
   mv $standalonepath/$foldername/* $standalonepath
   rmdir $standalonepath/$foldername
   nifi=${standalonepath}/
   sed -i .bak -e 's/nifi.web.http.port=8080/nifi.web.http.port=8084/g' $nifi/conf/nifi.properties
   sed -i .bak -e 's/#java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000/java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5004/g' $nifi/conf/bootstrap.conf


}
cluster()
{
  echo "setting non secure cluster"
  rm -rf ${clusterpath}
  file=$(basename "$tarPath")
  echo $clusterpath
  mkdir ${clusterpath}
  mkdir ${clusterpath}/nifi-1
  mkdir ${clusterpath}/nifi-2
  mkdir ${clusterpath}/nifi-3
  tar -C ${clusterpath}/nifi-1 -zxf $tarPath
  tar -C ${clusterpath}/nifi-2 -zxf $tarPath
  tar -C ${clusterpath}/nifi-3 -zxf $tarPath
  len=`expr ${#file} - 11`
  foldername=`echo $file|cut -c1-$len`
  mv ${clusterpath}/nifi-1/$foldername/* ${clusterpath}/nifi-1
  mv ${clusterpath}/nifi-2/$foldername/* ${clusterpath}/nifi-2
  mv ${clusterpath}/nifi-3/$foldername/* ${clusterpath}/nifi-3
  rmdir ${clusterpath}/nifi-1/$foldername
  rmdir ${clusterpath}/nifi-2/$foldername
  rmdir ${clusterpath}/nifi-3/$foldername

  ## configuring nifi-1
  nifi=${clusterpath}/nifi-1
  sed -i .bak -e 's/server.1=/#sever configuration/g' $nifi/conf/zookeeper.properties
  echo "server.1=localhost:2888:3888">>$nifi/conf/zookeeper.properties
  echo "server.2=localhost:2889:3889">>$nifi/conf/zookeeper.properties
  echo "server.3=localhost:2890:3890">>$nifi/conf/zookeeper.properties
  sed -i .bak -e 's/nifi.state.management.embedded.zookeeper.start=false/nifi.state.management.embedded.zookeeper.start=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.zookeeper.connect.string=/nifi.zookeeper.connect.string=localhost:2181,localhost:2182,localhost:2183/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.is.node=false/nifi.cluster.is.node=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.address=/nifi.cluster.node.address=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.protocol.port=/nifi.cluster.node.protocol.port=9997/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.host=/nifi.remote.input.host=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.socket.port=/nifi.remote.input.socket.port=8997/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.http.port=8080/nifi.web.http.port=8081/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.flow.election.max.candidates=/nifi.cluster.flow.election.max.candidates=1/g' $nifi/conf/nifi.properties

  sed -i .bak -e 's/#java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000/java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5001/g' $nifi/conf/bootstrap.conf

  mkdir -p $nifi/state/zookeeper
  echo "1">$nifi/state/zookeeper/myid
  mkdir -p $nifi/state/zookeeper/version-2

  ##configuration nifi-2
  nifi=${clusterpath}/nifi-2
  sed -i .bak -e 's/server.1=/#sever configuration/g' $nifi/conf/zookeeper.properties
  sed -i .bak -e 's/clientPort=2181/clientPort=2182/g' $nifi/conf/zookeeper.properties
  echo "server.1=localhost:2888:3888">>$nifi/conf/zookeeper.properties
  echo "server.2=localhost:2889:3889">>$nifi/conf/zookeeper.properties
  echo "server.3=localhost:2890:3890">>$nifi/conf/zookeeper.properties
  sed -i .bak -e 's/nifi.state.management.embedded.zookeeper.start=false/nifi.state.management.embedded.zookeeper.start=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.zookeeper.connect.string=/nifi.zookeeper.connect.string=localhost:2181,localhost:2182,localhost:2183/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.is.node=false/nifi.cluster.is.node=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.address=/nifi.cluster.node.address=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.protocol.port=/nifi.cluster.node.protocol.port=9998/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.host=/nifi.remote.input.host=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.socket.port=/nifi.remote.input.socket.port=8998/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.http.port=8080/nifi.web.http.port=8082/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.flow.election.max.candidates=/nifi.cluster.flow.election.max.candidates=1/g' $nifi/conf/nifi.properties
  mkdir -p $nifi/state/zookeeper
  echo "2">$nifi/state/zookeeper/myid
  mkdir -p $nifi/state/zookeeper/version-2

  sed -i .bak -e 's/#java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000/java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5002/g' $nifi/conf/bootstrap.conf


  ##configuration nifi-3
  nifi=${clusterpath}/nifi-3
  sed -i .bak -e 's/server.1=/#sever configuration/g' $nifi/conf/zookeeper.properties
  sed -i .bak -e 's/clientPort=2181/clientPort=2183/g' $nifi/conf/zookeeper.properties
  echo "server.1=localhost:2888:3888">>$nifi/conf/zookeeper.properties
  echo "server.2=localhost:2889:3889">>$nifi/conf/zookeeper.properties
  echo "server.3=localhost:2890:3890">>$nifi/conf/zookeeper.properties
  sed -i .bak -e 's/nifi.state.management.embedded.zookeeper.start=false/nifi.state.management.embedded.zookeeper.start=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.zookeeper.connect.string=/nifi.zookeeper.connect.string=localhost:2181,localhost:2182,localhost:2183/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.is.node=false/nifi.cluster.is.node=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.address=/nifi.cluster.node.address=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.protocol.port=/nifi.cluster.node.protocol.port=9999/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.host=/nifi.remote.input.host=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.socket.port=/nifi.remote.input.socket.port=8999/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.http.port=8080/nifi.web.http.port=8083/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.flow.election.max.candidates=/nifi.cluster.flow.election.max.candidates=1/g' $nifi/conf/nifi.properties
  mkdir -p $nifi/state/zookeeper
  echo "3">$nifi/state/zookeeper/myid
  mkdir -p $nifi/state/zookeeper/version-2

  sed -i .bak -e 's/#java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000/java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5003/g' $nifi/conf/bootstrap.conf

  #restart script
  echo "#!/bin/sh" >>${clusterpath}/restart-cluster.sh
  echo "cd ${clusterpath}nifi-1/bin/">>${clusterpath}/restart-cluster.sh
  echo "./nifi.sh restart">>${clusterpath}/restart-cluster.sh
  echo "cd ${clusterpath}nifi-2/bin/">>${clusterpath}/restart-cluster.sh
  echo "./nifi.sh restart">>${clusterpath}/restart-cluster.sh
  echo "cd ${clusterpath}nifi-3/bin/">>${clusterpath}/restart-cluster.sh
  echo "./nifi.sh restart">>${clusterpath}/restart-cluster.sh

  ## stop script
  echo "#!/bin/sh" >>${clusterpath}/stop-cluster.sh
  echo "cd ${clusterpath}nifi-1/bin/">>${clusterpath}/stop-cluster.sh
  echo "./nifi.sh stop">>${clusterpath}/stop-cluster.sh
  echo "cd ${clusterpath}nifi-2/bin/">>${clusterpath}/stop-cluster.sh
  echo "./nifi.sh stop">>${clusterpath}/stop-cluster.sh
  echo "cd ${clusterpath}nifi-3/bin/">>${clusterpath}/stop-cluster.sh
  echo "./nifi.sh stop">>${clusterpath}/stop-cluster.sh

  chmod -R 777 ${clusterpath}

}

if [ "$isHttps" == "true" ]; then
  secure
else
  nonsecure
fi

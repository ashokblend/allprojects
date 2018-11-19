#!/bin/sh

tarPath=/Users/ashok.kumar/github/nifi/nifi/nifi-assembly/target/nifi-1.9.0-SNAPSHOT-bin.tar.gz
toolkitTar=/Users/ashok.kumar/github/nifi/nifi/nifi-toolkit/nifi-toolkit-assembly/target/nifi-toolkit-1.9.0-SNAPSHOT-bin.tar.gz
isCluster=false
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
secure_zk_setup()
{
  nifi=$1
  clientPort=$2

  echo "Setting zookeeper for $nifi"
  sed -i .bak -e 's/server.1=/#sever configuration/g' $nifi/conf/zookeeper.properties
  sed -i .bak -e 's/clientPort=2181/clientPort='$clientPort'/g' $nifi/conf/zookeeper.properties
  echo "server.1=localhost:3888:4888">>$nifi/conf/zookeeper.properties
  echo "server.2=localhost:3889:4889">>$nifi/conf/zookeeper.properties
  echo "server.3=localhost:3890:4890">>$nifi/conf/zookeeper.properties

  sed -i .bak -e 's/nifi.state.management.embedded.zookeeper.start=false/nifi.state.management.embedded.zookeeper.start=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.zookeeper.connect.string=/nifi.zookeeper.connect.string=localhost:3181,localhost:3182,localhost:3183/g' $nifi/conf/nifi.properties

  mkdir -p $nifi/state/zookeeper/version-2
  echo $3>$nifi/state/zookeeper/myid
}
secure_cluster_setup()
{
  nifi=$1
  nodePort=$2
  remotePort=$3

  sed -i .bak -e 's/nifi.cluster.is.node=false/nifi.cluster.is.node=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.address=/nifi.cluster.node.address=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.protocol.port=/nifi.cluster.node.protocol.port='$nodePort'/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.host=/nifi.remote.input.host=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.socket.port=/nifi.remote.input.socket.port='$remotePort'/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.flow.election.max.candidates=/nifi.cluster.flow.election.max.candidates=1/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.protocol.is.secure=false/nifi.cluster.protocol.is.secure=true/g' $nifi/conf/nifi.properties

  sed -i .bak -e 's/<property name="Node Identity 1"><\/property>/<property name="Node Identity 1">UID=ashok-nifi, OU=nifi, CN=localhost<\/property><property name="Node Identity 2">UID=ashok-nifi, OU=nifi, CN=localhost<\/property><property name="Node Identity 3">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  #With above configuration you will be able to login to nifi ui using certificate but to get access, create policy by clicking access policy in root canvase

}
debug_setup()
{
  nifi=$1
  debugPort=$2
  #debug configuration
  sed -i .bak -e 's/#java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000/java.arg.debug=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address='$debugPort'/g' $nifi/conf/bootstrap.conf

}
kerberos_setup()
{
  nifi=$1
  sed -i .bak -e 's/<\/accessPolicyProvider>/<!--property name="Initial Admin Identity">ashok@HWX.COM<\/property--><\/accessPolicyProvider>/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<class>org.apache.nifi.authorization.FileUserGroupProvider<\/class>/<class>org.apache.nifi.authorization.FileUserGroupProvider<\/class><!--property name="Initial User Identity 2">ashok@HWX.COM<\/property-->/g' $nifi/conf/authorizers.xml
  #Manual configuration
  #1. enable kerberos provider in login-identity-providers.xml
  #2. provide Default realm for e.g HWX.COM
  #3. Enable Initial Admin Identity property in authorizers.xml e.g ashok@HWX.COM
  #4. Modify or add Initial User Identity <1 or 2> e.g ashok@HWX.COM
  #5. nifiprop: change nifi.security.user.login.identity.provider to kerberos-provider
  #6. nifiprop: add nifi.kerberos.service.principal
  #7. nifiprop: add nifi.kerberos.krb5.file
  #8. add nifi.kerberos.service.keytab.location
  #9. nifiprop: add nifi.kerberos.spnego.principal
  #10. nifiprop: add nifi.kerberos.spnego.keytab.location

}
ssl_setup()
{
  nifi=$1
  httpsPort=$2
  sed -i .bak -e 's/nifi.security.keystore=/nifi.security.keystore=\/Users\/ashok.kumar\/cluster\/nifi-clusters\/key\/nifi\/nifi_keystore.p12/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keystoreType=/nifi.security.keystoreType=jks/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keystorePasswd=/nifi.security.keystorePasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.keyPasswd=/nifi.security.keyPasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststore=/nifi.security.truststore=\/Users\/ashok.kumar\/cluster\/nifi-clusters\/key\/nifi\/nifi_truststore.p12/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststoreType=/nifi.security.truststoreType=jks/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.security.truststorePasswd=/nifi.security.truststorePasswd=Myself@1986/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.http.port=8080/nifi.web.http.port=/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.web.https.port=/nifi.web.https.port='$httpsPort'/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.secure=false/nifi.remote.input.secure=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/<property name="Initial User Identity 1"><\/property>/<property name="Initial User Identity 1">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
  sed -i .bak -e 's/<property name="Initial Admin Identity"><\/property>/<property name="Initial Admin Identity">UID=ashok-nifi, OU=nifi, CN=localhost<\/property>/g' $nifi/conf/authorizers.xml
}
cluster_script()
{
  clusterPath=$1
  #restart script
  echo "#!/bin/sh" >>${clusterPath}/restart-cluster.sh
  echo "cd ${clusterPath}nifi-1/bin/">>${clusterPath}/restart-cluster.sh
  echo "./nifi.sh restart">>${clusterPath}/restart-cluster.sh
  echo "cd ${clusterPath}nifi-2/bin/">>${clusterPath}/restart-cluster.sh
  echo "./nifi.sh restart">>${clusterPath}/restart-cluster.sh
  echo "cd ${clusterPath}nifi-3/bin/">>${clusterPath}/restart-cluster.sh
  echo "./nifi.sh restart">>${clusterPath}/restart-cluster.sh

  ## stop script
  echo "#!/bin/sh" >>${clusterPath}/stop-cluster.sh
  echo "cd ${clusterPath}nifi-1/bin/">>${clusterPath}/stop-cluster.sh
  echo "./nifi.sh stop">>${clusterPath}/stop-cluster.sh
  echo "cd ${clusterPath}nifi-2/bin/">>${clusterPath}/stop-cluster.sh
  echo "./nifi.sh stop">>${clusterPath}/stop-cluster.sh
  echo "cd ${clusterPath}nifi-3/bin/">>${clusterPath}/stop-cluster.sh
  echo "./nifi.sh stop">>${clusterPath}/stop-cluster.sh
}
securecluster()
{
  echo "setting secure cluster"
  rm -rf ${secureclusterpath}
  file=$(basename "$tarPath")
  echo $secureclusterpath
  mkdir -p ${secureclusterpath}
  mkdir -p ${secureclusterpath}/nifi-1
  mkdir -p ${secureclusterpath}/nifi-2
  mkdir -p ${secureclusterpath}/nifi-3
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
  ##zookeeper setup
  secure_zk_setup $nifi 3181 1
  #cluster setup
  secure_cluster_setup $nifi 7997 6997
  ssl_setup $nifi 9081
  debug_setup $nifi 6001
  ## ldap setup authorizers.xml
  ldap_setup $nifi
  #login-identity-providers.xml
  ldap_login_setup $nifi

  ##configuration nifi-2
  nifi=${secureclusterpath}/nifi-2
  ##zookeeper setup
  secure_zk_setup $nifi 3182 2
  secure_cluster_setup $nifi 7998 6998
  ssl_setup $nifi 9082
  debug_setup $nifi 6002
  ldap_setup $nifi
  ldap_login_setup $nifi

  ##configuration nifi-3
  nifi=${secureclusterpath}/nifi-3
  ##zookeeper setup
  secure_zk_setup $nifi 3183 3
  #cluster setup
  secure_cluster_setup $nifi 7999 6999
  ssl_setup $nifi 9083
  debug_setup $nifi 6003
  ## ldap setup authorizers.xml
  ldap_setup $nifi
  ldap_login_setup $nifi

  cluster_script ${secureclusterpath}

  chmod -R 777 ${secureclusterpath}
}

securestandalone()
{
    echo "setting secure standalone"
    rm -rf ${securestandalonepath}
    mkdir -p ${securestandalonepath}
    file=$(basename "$tarPath")
    tar -C $securestandalonepath/ -zxf $tarPath
    len=`expr ${#file} - 11`
    foldername=`echo $file|cut -c1-$len`
    mv $securestandalonepath/$foldername/* $securestandalonepath
    rmdir $securestandalonepath/$foldername
    nifi=$securestandalonepath
    ##configuring nifi.properties
    ssl_setup $nifi 9084
    ##kerberos setup disabled
    kerberos_setup $nifi
    debug_setup $nifi 6004
    ## ldap setup authorizers.xml
    ldap_setup $nifi
    ldap_login_setup $nifi

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
ldap_login_setup()
{
  echo "Setting ldap for $1"
  nifi=$1
  ## ldap setup login-identity-providers.xml
  sed -i .bak -e 's/<property name="Authentication Strategy">START_TLS/<property name="Authentication Strategy">SIMPLE/g' $nifi/conf/login-identity-providers.xml
  sed -i .bak -e 's/<property name="Manager DN">/<property name="Manager DN">uid=admin,ou=system/g' $nifi/conf/login-identity-providers.xml
  sed -i .bak -e 's/<property name="Manager Password">/<property name="Manager Password">secret/g' $nifi/conf/login-identity-providers.xml

  sed -i .bak -e 's/ldap-provider remove 2 lines. This is 1 of 2./ldap-provider remove 2 lines. This is 1 of 2. -->/g' $nifi/conf/login-identity-providers.xml
  sed -i .bak -e 's/To enable the ldap-provider remove 2 lines. This is 2 of 2./<!-- To enable the ldap-provider remove 2 lines. This is 2 of 2./g' $nifi/conf/login-identity-providers.xml
  sed -i .bak -e 's/<property name="Url">/<property name="Url">ldap:\/\/localhost:10389/g' $nifi/conf/login-identity-providers.xml
  sed -i .bak -e 's/<property name="User Search Base">/<property name="User Search Base">ou=nifi,dc=horton/g' $nifi/conf/login-identity-providers.xml
  sed -i .bak -e 's/<property name="User Search Filter">/<property name="User Search Filter">uid={0}/g' $nifi/conf/login-identity-providers.xml

  sed -i .bak -e 's/nifi.security.user.login.identity.provider=/nifi.security.user.login.identity.provider=ldap-provider/g' $nifi/conf/nifi.properties

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
   echo "setting non secure standalone"
   rm -rf ${standalonepath}
   mkdir -p ${standalonepath}
   file=$(basename "$tarPath")
   tar -C $standalonepath/ -zxf $tarPath
   len=`expr ${#file} - 11`
   foldername=`echo $file|cut -c1-$len`
   mv $standalonepath/$foldername/* $standalonepath
   rmdir $standalonepath/$foldername
   nifi=${standalonepath}/
   http_setup $nifi 8084
   debug_setup $nifi 5004

}
zk_setup()
{
  nifi=$1
  clientPort=$2
  sed -i .bak -e 's/server.1=/#sever configuration/g' $nifi/conf/zookeeper.properties
  sed -i .bak -e 's/clientPort=2181/clientPort='$clientPort'/g' $nifi/conf/zookeeper.properties
  echo "server.1=localhost:2888:3888">>$nifi/conf/zookeeper.properties
  echo "server.2=localhost:2889:3889">>$nifi/conf/zookeeper.properties
  echo "server.3=localhost:2890:3890">>$nifi/conf/zookeeper.properties
  sed -i .bak -e 's/nifi.state.management.embedded.zookeeper.start=false/nifi.state.management.embedded.zookeeper.start=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.zookeeper.connect.string=/nifi.zookeeper.connect.string=localhost:2181,localhost:2182,localhost:2183/g' $nifi/conf/nifi.properties

  mkdir -p $nifi/state/zookeeper/version-2
  echo $3>$nifi/state/zookeeper/myid
}
cluster_setup()
{
  nifi=$1
  nodePort=$2
  remotePort=$3
  sed -i .bak -e 's/nifi.cluster.is.node=false/nifi.cluster.is.node=true/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.address=/nifi.cluster.node.address=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.cluster.node.protocol.port=/nifi.cluster.node.protocol.port='$nodePort'/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.host=/nifi.remote.input.host=localhost/g' $nifi/conf/nifi.properties
  sed -i .bak -e 's/nifi.remote.input.socket.port=/nifi.remote.input.socket.port='$remotePort'/g' $nifi/conf/nifi.properties

  sed -i .bak -e 's/nifi.cluster.flow.election.max.candidates=/nifi.cluster.flow.election.max.candidates=1/g' $nifi/conf/nifi.properties
}
http_setup()
{
  nifi=$1
  httpPort=$2
  sed -i .bak -e 's/nifi.web.http.port=8080/nifi.web.http.port='$httpPort'/g' $nifi/conf/nifi.properties

}
cluster()
{
  echo "setting non secure cluster"
  rm -rf ${clusterpath}
  file=$(basename "$tarPath")
  echo $clusterpath
  mkdir -p ${clusterpath}
  mkdir -p ${clusterpath}/nifi-1
  mkdir -p ${clusterpath}/nifi-2
  mkdir -p ${clusterpath}/nifi-3
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

  zk_setup $nifi 2181 1
  cluster_setup $nifi 9997 8997
  http_setup $nifi 8081
  debug_setup $nifi 5001



  ##configuration nifi-2
  nifi=${clusterpath}/nifi-2
  zk_setup $nifi 2182 2
  cluster_setup $nifi 9998 8998
  http_setup $nifi 8082
  debug_setup $nifi 5002

  ##configuration nifi-3
  nifi=${clusterpath}/nifi-3
  zk_setup $nifi 2183 3
  cluster_setup $nifi 9999 8999
  http_setup $nifi 8083
  debug_setup $nifi 5003

  #restart script
  cluster_script $clusterpath

  chmod -R 777 ${clusterpath}

}

if [ "$isHttps" == "true" ]; then
  secure
else
  nonsecure
fi

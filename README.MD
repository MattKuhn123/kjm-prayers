# KJM Prayer Board

## Environment

### JVM args
1. -Denv=
1. -DdbUser=
1. -DdbPassword=
1. -DmailUser=
1. -DmailPassword=

### Tomcat

``` xml
<!-- Server.xml -->
<Connector port="8080" protocol="HTTP/1.1"
    connectionTimeout="20000"
    redirectPort="443" />
<!-- TODO : Verify the protocol -->
<Connector port="443" protocol="org.apache.coyote.http11.Http11AprProtocol"
    maxThreads="2000" SSLEnabled="true"
    scheme="https" secure="true"
    maxParameterCount="1000"
    maxHttpHeaderSize="65536"
>
    <SSLHostConfig protocols="TLSv1.2">
        <Certificate 
            certificateKeyFile="ssl\service.key"
            certificateFile="ssl\service.crt"
            certificateKeyPassword="password"
            type="RSA" />
    </SSLHostConfig>
</Connector>

```

### MySql

``` bash
docker run --env=MYSQL_ROOT_PASSWORD=........ --env=PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin --env=GOSU_VERSION=1.16 --env=MYSQL_MAJOR=innovation --env=MYSQL_VERSION=8.1.0-1.el8 --env=MYSQL_SHELL_VERSION=8.0.34-1.el8 --volume=/var/lib/mysql -p 3306:3306 -p 33060:33060 --runtime=runc -d mysql:latest
```
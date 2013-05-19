Description
===========

cmdsrv is a dummy command execution server written in python, backed by the 
[Bottle Python Webframerowk](http://bottlepy.org/docs/dev/).

It is used to invoke some commands to be processed on the underlying system and 
get back stderr, stdout and the return code value. 
It basically starts a simple http server which listens for incoming requests 
containing the command to process

The motivation for this tool is due to issues with 1.6 jvm which fork+exec
a new process for every supplied command. The memory of the child process is 
thus copied from the parent. If the parent process (the jvm instance) uses a 
huge amount of memory at the time the process is forked, the jvm exposes
itself to a 'Cannot allocate memory' error.

Other solutions (but discarded for infrastructure related concerns) are :
* tweek /proc/sys/vm/overcommit_memory to allow more memory allocation that 
actually exists on the system
* java 8 which proposes a 'spawn' mechanism istead of the current 'fork+exec' 
one

A command server over HTTP may seem an 'overkill' solution but :
* a java based solution is out de facto
* neither performance nor security were issues to take care of as I wrote this 
piece of code
* simplicity above all : bottle is a hell of a simple python web server
* testability improved via human readable json payload (you can curl the server
easily)
* I'm not an experienced Python developer
* I like HTTP and I like REST

Requirements
============

* Python 2.6+ (server part)
* Java 7 (client part)

Security Considerations
=======================

* cmdsrv runs the received commands as the user it has been launched with. 
Be sure to set the permissions as you want on your system
* althought the configuration file allows you to bind the server on any ip and 
port you may choose on the underlying system, cmdsrv has been developed in 
order to launch commands from a **local** and **trusted** client. As it is 
mentionned earlier, neither security nor performance were considered when 
designing cmdsrv. Memory usage was the only consideration.

Usage
=====

## server

    $ git clone https://github.com/cuberri/cmdsrv.git
    Cloning into 'cmdsrv'...
    [clipped...]

    $ cd cmdsrv/server && ./cmdsrv start
    Starting application...
    pid saved in /home/chris/workspace/cmdsrv/server/cmdsrv.pid : 3418

## testing curl client request

    $ curl -X POST "http://localhost:8055/cmd" -H "Content-Type:application/json" -d '{"cmd":["echo", "dummy", "cmdsrv"]}'
    {"cmd": ["echo", "dummy", "cmdsrv"], "retval": 0, "stderr": "", "stdout": "dummy cmdsrv\n"}

## using the `client-java` program

The project under `client-java` provides two ways of requesting the server

### build the jar

    $ mvn package

### embedded java client

A very basic java 7 client is packaged as the main class of the jar produced by
the maven build. It is intended to be used as testing purposes as it does not 
require any other java library depdendency :

    $ java -jar cmdsrv-client-0.1.0-SNAPSHOT.jar "http://127.0.0.1:8055/cmd" "{\"cmd\":[\"echo\",\"dummy\",\"cmdsrv\"]}"
    {"cmd": ["echo", "dummy", "cmdsrv"], "retval": 0, "stderr": "", "stdout": "dummy cmdsrv\n"}

### the programmatic rest client

The jar contains a REST client which can be customized with a preconfigured 
Jersey WebResource. The jar has some dependencies, see `pom.xml` for details :

    // usging Jersey client
    ClientConfig clientConfig = new DefaultClientConfig();
    Client client = Client.create(clientConfig);
    client.addFilter(new LoggingFilter());
    CmdsrvRestClientJersey sut = new CmdsrvRestClientJersey(client.resource("http://localhost:8055/cmd"));
    CmdsrvRequest req = new CmdsrvRequest("echo", "dummy", "cmdsrv");

console output

    19 mai 2013 19:07:06 com.sun.jersey.api.client.filter.LoggingFilter log
    INFO: 1 * Client out-bound request
    1 > POST http://localhost:8055/cmd
    1 > Content-Type: application/json
    1 > Hint-Content-Length: 35
    1 > Accept: application/json
    {"cmd":["echo", "dummy", "cmdsrv"]}

    19 mai 2013 19:07:06 com.sun.jersey.api.client.filter.LoggingFilter log
    INFO: 1 * Client in-bound response
    1 < 200
    1 < Content-Length: 76
    1 < Content-Type: application/json
    1 < Server: Jetty(7.3.0.v20110203)
    Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.029 sec
    1 <
    {"cmd": ["echo", "dummy", "cmdsrv"], "retval": 0, "stderr": "", "stdout": "dummy cmdsrv\n"}

Development
===========

This project has been made for a very specific use case where Java had
some anoying drawbacks. Please do not consider it as a production ready solution
for executing batch programs. Otherwise, feel free to improve it :)

License and Author
==================

* Author: Christophe Uberri (<cuberri@gmail.com>)

Copyright: 2013, Christophe Uberri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Simple Cassandra Client
=======================

Author: Chris Hedley

This application is a simple [Cassandra] [1] client which will attempt to connect to a provisioned [Cassandra] [1]
service in CloudFoundry. We assume that you already have Cassandra as a Service setup in your Cloudfoundry deployment.


Building
---------

The project is compiled and packaged using [Gradle] [2]. The project can be built using gradlew (*nix) or
gradle.bat (Windows) if you do not have [Gradle] [2] installed. Note, for the rest of this documentation we will refer
to Gradle commands as `gradle`. If you do not have Gradle installed then change the `gradle` command for that of your
environment.

Running the `gradle war`. following command will create a war file in `$PROJECT_HOME/build/libs`. This war can
be pushed to CloudFoundry using the [vmc] [3] tool.

Deploying
---------

The build script also support deploying an application and creating a Cassandra Service. To use this feature follow the below steps:

1. Open up build.gradle and add your Cloudfoundry username and password to the cloudfoundry hash (lines 42 & 43)
2. Change the target (line 41) and the URI's (line 46) if required.
3. Run the command `gradle war` to build the war artifact.
4. Run the command `gradle cf-add-service` to create the test Cassandra service.
5. Tun the command `gradle cf-push` tp push create the app in Cloudfoundry.

Note steps 2,3 and 4 can be replaced with the single command `gradlew war cf-add-service cf-push`

You should be left with a new Cassandra Service called 'cassandra-test-service' and a new application called simple-cassandra-client.
The application can be accessed at the urls http://c.cloudfoundry.test' and 'http://cassandra-test.cloudfoundry.test (interchange your
Cloudfoundry target in the urls where required.)


Copyright 2012 - CloudCredo Ltd.


[1]: http://cassandra.apache.org                                                        "Cassandra"
[2]: http://gradle.org                                                                  "Gradle"
[3]: http://docs.cloudfoundry.com/tools/vmc/installing-vmc.html                         "vmc"



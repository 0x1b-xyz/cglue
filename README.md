cglue
=====

CuriousGlue "cglue" is an operations and management tool for configuring and executing an application runtime that
 enables you to launch one or more server processes using a simple set of commands.

cglue solves the challenge of providing a lifecycle around applications that is consistent across environments: from
 development on to production. A simple set of files placed in a location within the application artifact is all that
 is required to make use of the cglue command line interface - while the Maven plugin goals provide hooks to implement
 and test your app lifecycle.

An "app" could consist of any sort of process - a database server, a tomcat instance, a local file processing daemon,
 etc..

Composition
-----

### cglue-api

Provides a set of interfaces that can be used in implementing your [App](cglue/api/src/main/java/cglue/App.java). The
 responsibility of an app is to instantiate the [Server](cglue/api/src/main/java/cglue/Server.java) which wraps around
 the process being managed by the framework.

An Application
-----

An app originates from an [Artifact](cglue/api/src/main/java/cglue/Artifact.java) where cglue will be able to find the
 [`META-INF/cglue/app.properties`](cglue/api/src/main/resources/cglu/app.properties) file that configures the runtime to
 execute the lifecycle of your [App](cglue/api/src/main/java/cglue/App.java) implementation.

```groovy
    @Log
    class MyWebApp implements cglue.App {

        final Go go

        MyWebApp(Go go) {
           this.go = go
        }

        Server initialize() {
           return new TomcatServer(go)
        }
        void starting() {
            ...
            log.info("Configuring the dispatcher.xml file ...")
            go.config.bind(go.docBase.read('META-INF/cglue/myapp/dispatcher.xml'), go.docBase.write('WEB-INF/classes/dispatcher.xml'))
        }
        void stopping() {
            log.info("Making a backup of the app home directory ...")
            go.ant.copy(dir: go.appHome, dest: new File(go.config.props['myapp.backupDir']))
        }

    }
````

Configuration
-----

Configuration is sourced from the `app.properties` file of your artifact. This file contains the "default" configuration
 that will resolved on a per attribute basis by the [Config](cglue/api/src/main/java/cglue/Config) interface using this
 order:

* Environment
* System Property
* Command line switch or Maven configuration override
* `META-INF/cglue/app.properties`

The configuration is used to make decisions within the `App` implementation. Another common use is to write the elements
 out to a file that can be accessed by your runtime application.

Usage
-----

### Command line

    $ export CGLUE_HOME=/opt/cglue

    $ cglue exampleapp start
    -----> [Main] CuriousGlue 1.0, artifact: cglue/artifacts/exampleapp.war
    -----> [Glue] Initializing example.app.ExampleApp
    -----> [ExampleApp] Initializing cglue.server.tomcat.TomcatServer
    -----> [TomcatServer] Checking for running server on control port 8099
    -----> [Glue] Expanding cglue/artifacts/exampleapp.war into cglue/work/exampleapp/f34fd334sd4f
    -----> [Glue] Calling ExampleApp#starting()
    -----> [ExampleApp] Configuring cglue/app/exampleapp/myconfiguration.properties
    -----> [ExampleApp] Acquiring connection to jdbc:mysql://localhost:3306/sakila?profileSQL=true
    -----> [ExampleApp] Updating database tables ...
    -----> [TomcatServer] Dispatching to external process

    $ cglue exampleapp status
    -----> [Main] CuriousGlue 1.0, artifact: cglue/artifacts/exampleapp.war
    -----> [Glue] Initializing example.app.ExampleApp
    -----> [ExampleApp] Initializing cglue.server.tomcat.TomcatServer
    -----> [TomcatServer] Checking for running server on control port 8099
    -----> [Glue] Server is running on port 8080

    $ cglue exampleapp stop
    -----> [Main] CuriousGlue 1.0, artifact: cglue/artifacts/exampleapp.war
    -----> [Glue] Initializing example.app.ExampleApp
    -----> [ExampleApp] Initializing cglue.server.tomcat.TomcatServer
    -----> [TomcatServer] Stopping external process

### From Maven

    mvn cglue:prepare

Running the prepare goal will "instrument" your Maven module artifact with the contents of the `src/main/go` directory
that contains the application [App](cglue/api/src/main/java/cglue/App.java) implementation and your
[app.properties](cglue/api/src/main/resources/cglue/app.properties) like so:

    exampleapp/
        src/main/cglue
            ExampleApp.groovy
            app.properties














### License

Copyright 2012 Jason Stiefel <jason@stiefel.io>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
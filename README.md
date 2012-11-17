cglue
=====

CuriousGlue "cglue" is an operations and management tool for configuring and executing an application runtime that
enables you to launch one or more server processes using a simple set of commands.

cglue solves the challenge of providing a lifecycle around application phases that is consistent across environments
from development to production. An "app" could consist of any sort of process - a database server, a tomcat instance,
etc.. An app is composed of an [api/src/main/java/cglue/Artifact.java] where we'll find the
`META-INF/cglue/app.properties` file that gives us the path to your [api/src/main/java/cglue/App.java] implementation.

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
that contains the application [api/src/main/java/cglue/App.java] implementation and your
[api/src/main/resources/cglue/app.properties] like so:













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
<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->

# Component Plugin for Apache Taverna

Plugins for
[Apache Taverna](http://taverna.incubator.apache.org/) for
supporting components.

[Taverna Workflow Components](doc/) are a system for creating shareable, 
reusable, encapsulated sub-workflows that perform clearly defined tasks while 
abstracting the details of how those tasks are performed.


This plugin is **no longer maintained** by the Apache Taverna project, but 
has been made available to [taverna-extras](https://github.com/taverna-extras/) 
for archival purposes, open for third-party contributions.

This module relies on official
[Apache Taverna modules](http://taverna.incubator.apache.org/code) for
the actual workflow execution.



## License

(c) 2013-2014 University of Manchester

(c) 2014-2018 Apache Software Foundation

This product includes software developed at the
[Apache Software Foundation](http://www.apache.org/).

Licensed under the
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), see the file
[LICENSE](LICENSE) for details.

The file [NOTICE](NOTICE) contain any additional attributions and
details about embedded third-party libraries and source code.



# Contribute

This plugin is **not actively maintained**, but feel free to raise a
[GitHub pull request](https://github.com/taverna-extras/taverna-plugin-component/pulls).

Any contributions received are assumed to be covered by the 
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0). 


## Prerequisites

* Java 1.7 or newer (tested with OpenJDK 1.8)
* [Apache Maven](https://maven.apache.org/download.html) 3.2.5 or newer (older
  versions probably also work)


# Building

To build, use

    mvn clean install

This will build each module and run their tests.


## Building on Windows

If you are building on Windows, ensure you unpack this source code
to a folder with a [short path name](http://stackoverflow.com/questions/1880321/why-does-the-260-character-path-length-limit-exist-in-windows) 
lenght, e.g. `C:\src` - as 
Windows has a [limitation on the total path length](https://msdn.microsoft.com/en-us/library/aa365247%28VS.85%29.aspx#maxpath) 
which might otherwise
prevent this code from building successfully.


## Skipping tests

To skip the tests (these can be timeconsuming), use:

    mvn clean install -DskipTests


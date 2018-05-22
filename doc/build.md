# Build efa using maven

### Install 3rd-party libs to local repository
Execute this from the project root:
```
mvn install:install-file -Dfile=plugins/jsuntimes/jsuntimes.jar -DgroupId=uk.me.jstott \
-DartifactId=coordconv -Dversion=1.0.0 -Dpackaging=jar 
```

### Building a jar file
A jar package can be created with `mvn package`

### Testing
To simply test one module execute one of the following commands:
```
mvn exec:java@Main
mvn exec:java@Boathouse
mvn exec:java@API
```
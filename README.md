# gRPC Java Server and Node.js Client Demo

This project demonstrates a simple gRPC application with a Java server and a Node.js client.

## Java Server Setup

### Prerequisites

- Eclipse IDE
- Maven
- Java 8 or higher

### Steps

1. **Create a Maven Project in Eclipse**: Start by creating a new Maven project in Eclipse.

2. **Add gRPC Dependencies**: In your `pom.xml`, include the following dependencies:

    ```xml
    <dependencies>
    		<dependency>
    		    <groupId>com.google.protobuf</groupId>
    		    <artifactId>protobuf-java</artifactId>
    		    <version>3.25.0</version>
    		</dependency>
    		<dependency>
    		    <groupId>io.grpc</groupId>
    		    <artifactId>grpc-netty-shaded</artifactId>
    		    <version>1.59.0</version>
    		</dependency>
    		<dependency>
    		    <groupId>io.grpc</groupId>
    		    <artifactId>grpc-protobuf</artifactId>
    		    <version>1.59.0</version>
    		</dependency>
    		<dependency>
    		    <groupId>io.grpc</groupId>
    		    <artifactId>grpc-stub</artifactId>
    		    <version>1.59.0</version>
    		</dependency>
    		<dependency>
    		    <groupId>one.gfw</groupId>
    		    <artifactId>javax.annotation-api</artifactId>
    		    <version>1.3.2</version>
    		</dependency>
	 </dependencies>
    ```

3. **Create `greeter.proto` File**: Inside `src/main/resources`, create a file named `greeter.proto` and add your gRPC service definitions:

    ```proto
    syntax = "proto3";

    option java_package = "com.sample.grpc";

    service Greeter{
        rpc sayHi(Request) returns (Response);
    }

    message Request{
        string name = 1;
    }

    message Response{
        string message = 1;
        int32 responseCode = 2;
    }
    ```

4. **Install Protobuf-DT**: To get autocomplete for `.proto` files, install the Protobuf-DT extension from the Eclipse Marketplace.

5. **Configure Protobuf and gRPC Code Generation**: Add the following to your `pom.xml` to generate the server code:

    ```xml
    <properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
   <build>
  		<defaultGoal>clean generate-sources compile install</defaultGoal>
  
  		<plugins>
  			<!-- compile proto file into java files. -->
  			<plugin>
  				<groupId>com.github.os72</groupId>
  				<artifactId>protoc-jar-maven-plugin</artifactId>
  				<version>3.11.4</version>
  				<executions>
  					<execution>
  						<phase>generate-sources</phase>
  						<goals>
  							<goal>run</goal>
  						</goals>
  						<configuration>
  						   <includeMavenTypes>direct</includeMavenTypes>
  							
  							<inputDirectories>
  								<include>src/main/resources</include>
  							</inputDirectories>
  							
  							<outputTargets>
  								<outputTarget>
  									<type>java</type>
  									<outputDirectory>src/main/java</outputDirectory>
  								</outputTarget>
  								<outputTarget>
  									<type>grpc-java</type>
  									<pluginArtifact>io.grpc:protoc-gen-grpc-java:1.15.0</pluginArtifact>
  									<outputDirectory>src/main/java</outputDirectory>
  								</outputTarget>
  							</outputTargets>
  						</configuration>
  					</execution>
  				</executions>
  			</plugin>
  
  
  			<plugin>
  				<groupId>org.apache.maven.plugins</groupId>
  				<artifactId>maven-compiler-plugin</artifactId>
  				<version>3.8.0</version>
  				<configuration>
  					<source>1.8</source>
  					<target>1.8</target>
  				</configuration>
  			</plugin>
  		</plugins>
  	</build>
    ```

6. **Generate Code**: Right-click on the `pom.xml` in Eclipse and build with Maven to generate the gRPC Java files in the `com.sample.grpc` package.

7. **Create `GreeterService.java`**: Inside `com.sample.grpc.services`, create `GreeterService.java` with the server logic:

    ```java
    package com.sample.grpc.services;

    import com.sample.grpc.GreeterGrpc.GreeterImplBase;
    import com.sample.grpc.GreeterOuterClass.*;

    import io.grpc.stub.StreamObserver;

    public class GreeterService extends GreeterImplBase {

        @Override
        public void sayHi(Request request, StreamObserver<Response> responseObserver) {
            Response.Builder response = Response.newBuilder();
            if (!request.getName().trim().isEmpty())
                response.setMessage("Hello " + request.getName()).setResponseCode(200);
            else
                response.setMessage("Please enter your name!").setResponseCode(400);
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }
    ```

8. **Set Up Server Runner (`App.java`)**: In the `test` package, create `App.java` to run the server on port 1000:

    ```java
    package com.sample.grpc.test;

    import com.sample.grpc.services.GreeterService;

    import io.grpc.ServerBuilder;
    import io.grpc.*;

    public class App {
        public static void main(String[] args) {
            Server server = ServerBuilder.forPort(1000).addService(new GreeterService()).build();
            try {
                server.start();
                System.out.println("server started at " + server.getPort());
                server.awaitTermination();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    ```

9. **Start the Server**: Run `App.java` to start the Java gRPC server.

## Node.js Client Setup

### Prerequisites

- Node.js
- npm

### Steps

1. **Create a New Node.js Project**: Inside a new folder named `Client`, initialize a Node.js project using `npm init`.

2. **Install gRPC Packages**: Install `@grpc/grpc-js` and `@grpc/proto-loader` packages.

    ```bash
    npm install @grpc/grpc-js @grpc/proto-loader
    ```

3. **Set Up `greeting.proto`**: Copy the `greeting.proto` file from the Java project into the `Client` folder.

4. **Create `app.js`**: Inside the `Client` folder, create `app.js` with the client logic to call the gRPC service:

    ```javascript
    const grpc = require('@grpc/grpc-js');
    const protoLoader = require('@grpc/proto-loader');

    // Load the .proto file
    const packageDefinition = protoLoader.loadSync('greeting.proto', {
      keepCase: true,
      defaults: true,
      oneofs: true
    });

    // Load the service definition from the .proto file
    const greeterService = grpc.loadPackageDefinition(packageDefinition).Greeter;

    // Create a new client instance
    const client = new greeterService('localhost:1000', grpc.credentials.createInsecure());

    // Call the service method
    client.sayHi({name: 'Chaimae'}, function(error, response) {
      if (error) {
        console.error(error);
      } else {
        console.log('Message:', response.message);
        console.log('Response Code:', response.responseCode);
      }
    });
    ```

5. **Run the Client**: Use `node app.js` to run the client script and test the gRPC call.

## Testing with BloomRPC

- Import the `greeting.proto` file into BloomRPC.
- Set the target server to `localhost:1000`.
- Test the gRPC service.

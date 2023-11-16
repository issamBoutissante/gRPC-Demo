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

package com.sample.grpc.services;


import com.sample.grpc.GreeterGrpc.GreeterImplBase;
import com.sample.grpc.GreeterOuterClass.*;

import io.grpc.stub.StreamObserver;

public class GreeterService extends GreeterImplBase {

	@Override
	public void sayHi(Request request, StreamObserver<Response> responseObserver) {
		Response.Builder response=Response.newBuilder();
		if(request.getName().trim()!="")
			response.setMessage("Hello "+request.getName()).setResponseCode(200);
		else
			response.setMessage("Please enter your name!").setResponseCode(400);
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
	}
	
}

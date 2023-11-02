package com.sample.grpc.test;

import com.sample.grpc.services.GreeterService;

import io.grpc.ServerBuilder;
import io.grpc.*;

public class App {
	public static void main(String[] args) {
		Server server = ServerBuilder.forPort(1000).addService(new GreeterService()).build();
		try {
			server.start();
			System.out.println("server started at "+ server.getPort());
			server.awaitTermination();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

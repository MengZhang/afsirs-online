package com.mkyong.rest;
 
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
 
@Path("/hello")
public class HelloWorldService {
 
//	@GET
//	@Path("/{param}")
//	public Response getMsg(@PathParam("param") String msg) {
// 
//		String output = "Jersey say : " + msg;
// 
//		return Response.status(200).entity(output).build();
// 
//	}
	@GET
	@Path("/stations")
	public Response decode(String shortURL) throws URISyntaxException {

		return Response.status(Status.OK).entity("TAMPA,ORLANDO").build();


	}
 
}
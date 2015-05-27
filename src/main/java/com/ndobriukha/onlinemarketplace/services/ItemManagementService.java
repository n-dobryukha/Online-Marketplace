package com.ndobriukha.onlinemarketplace.services;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/items")
public class ItemManagementService {
	
	@GET
	@Path("show")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getItems(@Context HttpServletRequest req) throws URISyntaxException {
		req.getServletContext().log("/items/show");
		//return Response.seeOther(new URI("adsa")).build();
		return Response.ok().build();
	}
}
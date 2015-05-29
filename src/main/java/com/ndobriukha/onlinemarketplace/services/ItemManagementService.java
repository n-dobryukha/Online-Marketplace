package com.ndobriukha.onlinemarketplace.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("/")
public class ItemManagementService {
	
	@GET
	@Path("/show")
	@Produces(MediaType.TEXT_HTML)
	public Viewable show() {
		return new Viewable("/showitems");
	}
	
	@POST
	@Path("/show/all")
	@Produces(MediaType.TEXT_HTML)
	public String getAll() {
		return "{"
				+"\"data\": ["
				+"{"
				+"\"uid\": \"1\","
				+"\"title\": \"A parrot\","
				+"\"description\": \"Very beautiful bird\","
				+"\"seller\": {"
				+"\"id\": 1,"
				+"\"name\": \"Mr Smith\""
				+"},"
				+"\"startPrice\": \"10.00\","
				+"\"bidInc\": \"1.50\","
				+"\"bestOffer\": \"15.00\","
				+"\"bidder\": {"
				+"\"id\": 4,"
				+"\"name\": \"Mrs Jonsen\""
				+"},"
				+"\"stopDate\": {"
				+"\"display\": \"24.11.2007 11:00\","
				+"\"timestamp\": \"1195902000\""
				+"},"
				+"\"action\": \"bid\""
				+"}]}";
	}
}
package ru.hse.avrogen.controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/avroGenerator/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AvroGeneratorController {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/generateSchema")
    public String hello() {
        return "Initial result string";
    }
}
package ru.hse.avrogen.controller;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.common.NotImplementedYet;
import ru.hse.avrogen.service.AvroCRUDService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/avroGenerator/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AvroGeneratorController {

    @Inject
    AvroCRUDService avroCRUDService;

    @POST
    @Path("/createSchema")
    public Uni<String> createAvroSchema() {
        throw new NotImplementedYet();
    }

    @GET
    @Path("/getSchemas")
    public Uni<List<String>> getAvroSchemas() {
        return avroCRUDService.getAvroSchemas();
    }

    @GET
    @Path("/getSubjects")
    public Uni<List<String>> getSubjects() {
        return avroCRUDService.getSubjects();
    }
}
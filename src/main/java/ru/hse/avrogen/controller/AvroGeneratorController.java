package ru.hse.avrogen.controller;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestQuery;
import ru.hse.avrogen.dto.GetSqlToAvroDto;
import ru.hse.avrogen.dto.PostCreateSchemaBodyDto;
import ru.hse.avrogen.dto.PostSchemaResponseDto;
import ru.hse.avrogen.dto.SubjectInfoDto;
import ru.hse.avrogen.service.AvroCRUDService;
import ru.hse.avrogen.service.SqlToAvroService;

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
    @Inject
    SqlToAvroService sqlToAvroService;

    @POST
    @Path("/createSchema")
    public Uni<PostSchemaResponseDto> createAvroSchema(PostCreateSchemaBodyDto schemaCreationData) {
        return avroCRUDService.createSchema(schemaCreationData.subjectName(), schemaCreationData.schema());
    }

    @GET
    @Path("/getSchemaVersions")
    // Todo: change to another API format?
    public Uni<List<Integer>> getAvroSchemas(@RestQuery String subject) {
        return avroCRUDService.getSchemasBySubject(subject);
    }

    @GET
    @Path("/getSubjects")
    public Uni<List<String>> getSubjects() {
        return avroCRUDService.getSubjects();
    }

    @GET
    @Path("/sqlToAvro")
    public Uni<GetSqlToAvroDto> getAvroSchemaBySql(String sql) {
        return sqlToAvroService.convertSqlToAvro(sql);
    }

    @DELETE
    @Path("/deleteSubject")
    public Uni<List<Integer>> deleteSubject(SubjectInfoDto subjectDto) {
        return avroCRUDService.deleteSubject(subjectDto.subjectName());
    }
}
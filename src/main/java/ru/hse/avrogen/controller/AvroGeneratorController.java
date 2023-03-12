package ru.hse.avrogen.controller;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import ru.hse.avrogen.dto.*;
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
    @Path("/getSchema/{subjectName}/{schemaVersion}")
    public Uni<GetSchemaInfoDto> getSchemaByVersion(@RestPath String subjectName, @RestPath String schemaVersion) {
        return avroCRUDService.getAvroSchemas(subjectName, schemaVersion);
    }

    // Todo: is this actually a GET endpoint?
    @GET
    @Path("/sqlToAvro")
    public Uni<GetSqlToAvroDto> getAvroSchemaFromSql(String sql) {
        return sqlToAvroService.convertSqlToAvro(sql);
    }

    @DELETE
    @Path("/deleteSubject")
    public Uni<List<Integer>> deleteSubject(SchemaSubjectInfoDto schemaSubjectInfoDto) {
        return avroCRUDService.deleteSubject(schemaSubjectInfoDto.subjectName());
    }

    @DELETE
    @Path("/deleteSchema")
    public Uni<Integer> deleteSchemaVersion(SchemaSubjectInfoDto schemaSubjectInfoDto) {
        return avroCRUDService.deleteSchemaVersion(schemaSubjectInfoDto.subjectName(),
                schemaSubjectInfoDto.schemaVersion());
    }
}
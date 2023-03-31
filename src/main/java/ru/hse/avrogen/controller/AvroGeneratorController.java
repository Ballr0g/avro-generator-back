package ru.hse.avrogen.controller;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;
import ru.hse.avrogen.dto.*;
import ru.hse.avrogen.dto.responses.DeleteSchemaVersionResponseDto;
import ru.hse.avrogen.dto.responses.DeleteSubjectResponseDto;
import ru.hse.avrogen.dto.responses.GetSchemaVersionsResponseDto;
import ru.hse.avrogen.dto.responses.GetSubjectsResponseDto;
import ru.hse.avrogen.service.AvroCRUDService;
import ru.hse.avrogen.service.SqlToAvroService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    @Path("/getSchemaVersions/{subjectName}")
    public Uni<GetSchemaVersionsResponseDto> getAvroSchemaVersions(@RestPath String subjectName) {
        return avroCRUDService.getSchemasBySubject(subjectName);
    }

    @GET
    @Path("/getSubjects")
    public Uni<GetSubjectsResponseDto> getSubjects() {
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
    @Path("/deleteSubject/{subjectName}")
    public Uni<DeleteSubjectResponseDto> deleteSubject(@RestPath String subjectName) {
        return avroCRUDService.deleteSubject(subjectName);
    }

    @DELETE
    @Path("/deleteSchema/{subjectName}/{schemaVersion}")
    public Uni<DeleteSchemaVersionResponseDto> deleteSchemaVersion(@RestPath String subjectName,
                                                                   @RestPath String schemaVersion) {
        return avroCRUDService.deleteSchemaVersion(subjectName, schemaVersion);
    }
}
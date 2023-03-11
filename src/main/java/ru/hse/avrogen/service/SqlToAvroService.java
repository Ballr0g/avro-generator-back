package ru.hse.avrogen.service;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.common.NotImplementedYet;
import ru.hse.avrogen.dto.GetSqlToAvroDto;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SqlToAvroService {
    public Uni<GetSqlToAvroDto> convertSqlToAvro(String sql) {
        throw new NotImplementedYet();
    }
}

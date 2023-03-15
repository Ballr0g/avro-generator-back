package ru.hse.avrogen.util.schema.avro;

import org.apache.avro.Schema;
import org.jboss.resteasy.reactive.common.NotImplementedYet;
import ru.hse.avrogen.dto.FieldRequirementsDto;
import ru.hse.avrogen.dto.builders.FieldRequirementsBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class AvroSchemaValidationStrategyImpl implements AvroSchemaValidationStrategy {
    private final FieldRequirementsBuilder fieldRequirementsBuilder = new FieldRequirementsBuilder();

    // Todo: setup the nesting
    private final FieldRequirementsDto rootFieldRequirement = fieldRequirementsBuilder.build();


    @Override
    public boolean tryValidateSchema(Schema schema) {
        throw new NotImplementedYet();
    }

    @Override
    public List<String> getMandatoryFields() {
        return null;
    }
}

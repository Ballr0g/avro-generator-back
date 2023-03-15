package ru.hse.avrogen.dto.builders;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FieldRequirementsBuilder {
    private Schema.Type fieldType;
    private String fieldName;
    private List<String> requiredFieldNames;
    private List<FieldRequirementsDto> nestedFieldRequirements;

    public FieldRequirementsBuilder() {
        requiredFieldNames = new ArrayList<>();
        nestedFieldRequirements = new ArrayList<>();
    }

    public FieldRequirementsBuilder requiredFieldNames(List<String> requiredFieldNames) {
        this.requiredFieldNames = requiredFieldNames;
        return this;
    }

    public FieldRequirementsBuilder addRequiredFieldName(String requiredFieldName) {
        requiredFieldNames.add(requiredFieldName);
        return this;
    }

    public FieldRequirementsBuilder nestedFieldRequirements(List<FieldRequirementsDto> nestedFieldRequirements) {
        this.nestedFieldRequirements = nestedFieldRequirements;
        return this;
    }

    public FieldRequirementsBuilder addNestedFieldRequirement(FieldRequirementsDto nestedFieldRequirement) {
        nestedFieldRequirements.add(nestedFieldRequirement);
        return this;
    }

    public FieldRequirementsBuilder fieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public FieldRequirementsBuilder fieldType(Schema.Type fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public FieldRequirementsDto build() {
        throwOnUnsatisfiedRequirements();

        return new FieldRequirementsDto(fieldType, fieldName,
                requiredFieldNames.stream().toList(), nestedFieldRequirements.stream().toList());
    }

    public FieldRequirementsBuilder reset() {
        fieldType = null;
        fieldName = null;
        requiredFieldNames = new ArrayList<>();
        nestedFieldRequirements = new ArrayList<>();

        return this;
    }

    private void throwOnUnsatisfiedRequirements() {
        if (Objects.isNull(fieldName) || Objects.isNull(fieldType)) {
            throw new IllegalStateException("Field type and field name cannot be null");
        }
    }
}

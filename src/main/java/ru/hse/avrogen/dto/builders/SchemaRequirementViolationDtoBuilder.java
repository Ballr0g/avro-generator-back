package ru.hse.avrogen.dto.builders;

public class SchemaRequirementViolationDtoBuilder {
    // Todo: new implementation
//    private Schema.Type fieldType;
//    private String fieldName;
//    private List<String> requiredFieldNames;
//    private List<FieldRequirementViolationDto> nestedFieldRequirements;
//
//    public FieldRequirementsBuilder() {
//        requiredFieldNames = new ArrayList<>();
//        nestedFieldRequirements = new ArrayList<>();
//    }
//
//    public FieldRequirementsBuilder requiredFieldNames(List<String> requiredFieldNames) {
//        this.requiredFieldNames = requiredFieldNames;
//        return this;
//    }
//
//    public FieldRequirementsBuilder addRequiredFieldName(String requiredFieldName) {
//        requiredFieldNames.add(requiredFieldName);
//        return this;
//    }
//
//    public FieldRequirementsBuilder nestedFieldRequirements(List<FieldRequirementViolationDto> nestedFieldRequirements) {
//        this.nestedFieldRequirements = nestedFieldRequirements;
//        return this;
//    }
//
//    public FieldRequirementsBuilder addNestedFieldRequirement(FieldRequirementViolationDto nestedFieldRequirement) {
//        nestedFieldRequirements.add(nestedFieldRequirement);
//        return this;
//    }
//
//    public FieldRequirementsBuilder fieldName(String fieldName) {
//        this.fieldName = fieldName;
//        return this;
//    }
//
//    public FieldRequirementsBuilder fieldType(Schema.Type fieldType) {
//        this.fieldType = fieldType;
//        return this;
//    }
//
//    public FieldRequirementViolationDto build() {
//        throwOnUnsatisfiedRequirements();
//
//        return new FieldRequirementViolationDto(fieldType, fieldName,
//                requiredFieldNames.stream().toList(), nestedFieldRequirements.stream().toList());
//    }
//
//    public FieldRequirementsBuilder reset() {
//        fieldType = null;
//        fieldName = null;
//        requiredFieldNames = new ArrayList<>();
//        nestedFieldRequirements = new ArrayList<>();
//
//        return this;
//    }
//
//    private void throwOnUnsatisfiedRequirements() {
//        if (Objects.isNull(fieldName) || Objects.isNull(fieldType)) {
//            throw new IllegalStateException("Field type and field name cannot be null");
//        }
//    }
}

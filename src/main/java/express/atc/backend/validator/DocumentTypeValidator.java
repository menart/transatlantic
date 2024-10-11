package express.atc.backend.validator;

import express.atc.backend.enums.DocumentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentTypeValidator implements ConstraintValidator<DocumentTypeValid, Integer> {
    @Override
    public boolean isValid(Integer s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            return DocumentType.getDocumentTypeById(s) != null;
        } catch (Exception e) {
            return false;
        }
    }
}

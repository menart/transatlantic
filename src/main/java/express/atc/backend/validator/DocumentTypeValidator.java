package express.atc.backend.validator;

import express.atc.backend.enums.DocumentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentTypeValidator implements ConstraintValidator<DocumentTypeValid, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            return DocumentType.getDocumentTypeById(Integer.parseInt(s)) != null;
        } catch (Exception e) {
            return false;
        }
    }
}

package express.atc.backend.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = DocumentTypeValidator.class)
@Documented
public @interface DocumentTypeValid {
    String message() default "{DocumentTypeValid.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

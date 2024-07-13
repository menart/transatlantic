package express.atc.backend.exception;

public class ValidationMessage {

    public static final String PHONE_NOT_VALID =
            "Вы ввели неправильный номер, номер должен быть: +7 (___) ___ - __ - __, например: +7(999)888-44-55";
    public static final String DISAGREE = "Необходимо предоставить согласие";

    public static final String VALIDATE_CODE = "В коде должны быть только цифры";
}

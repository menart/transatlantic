package express.atc.backend.exception;

public class ValidationMessage {

    public static final String PHONE_NOT_VALID =
            "Вы ввели неправильный номер, номер должен быть: +7 (___) ___ - __ - __, например: +7(999)888-44-55";
    public static final String DISAGREE = "Необходимо предоставить согласие";

    public static final String VALIDATE_CODE = "В коде должны быть только цифры";
    public static final String INN_NOT_VALID = "ИНН должен состоять из 12 цифр";
    public static final String EMAIL_NOT_VALID = "Адрес электронной почты не валиден";
    public static final String DATE_NOT_VALID = "Не верный формат даты";
    public static final String DOC_TYPE_NOT_VALID = "Не верный тип документа";
    public static final String NOT_VALID_SIZE = "длина поля должно находиться в диапазоне между {min} и {max}";
}

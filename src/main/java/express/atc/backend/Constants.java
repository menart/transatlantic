package express.atc.backend;

public class Constants {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTH_HEADER_NAME = "Authorization";
    public static final String LOCATE_CURRENCY = "RUB";
    public static final long GRAMS_PER_KG = 1000;

    public static final String STRING_FEE = "Таможенная пошлина";
    public static final String STRING_TAX = "Сбор за таможенное оформление";
    public static final String STRING_COMPENSATION = "Комиссия таможенного представителя";

    public static final String CALC_WEIGHT = "Расчет по весу";
    public static final String CALC_AMOUNT = "Расчет по сумме";

    //Exception message
    public static final String TRACK_NOT_FOUND = "трек номер не найден";
    public static final String EMAIL_NOT_GIVEN = "email не заполнен";

    //Validation message
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

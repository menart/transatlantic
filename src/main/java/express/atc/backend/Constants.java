package express.atc.backend;

public class Constants {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTH_HEADER_NAME = "Authorization";
    public static final String LANG_HEADER_NAME = "Current-Language";
    public static final String LOCATE_CURRENCY = "RUB";
    public static final long GRAMS_PER_KG = 1000;

    public static final String STRING_FEE = "Таможенная пошлина";
    public static final String STRING_TAX = "Сбор за таможенное оформление";
    public static final String STRING_COMPENSATION = "Комиссия таможенного представителя";

    public static final String CALC_WEIGHT = "Расчет по весу";
    public static final String CALC_AMOUNT = "Расчет по сумме";

    //Exception message
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String USER_ALREADY_REGISTERED = "Пользователь уже зарегистрирован";
    public static final String PASSWORD_NOT_CONFIRMED = "Пароли не совпадают";
    public static final String TRACK_NOT_FOUND = "трек номер не найден";
    public static final String ORDER_NOT_FOUND = "Заказ не найден";
    public static final String EMAIL_NOT_GIVEN = "email не заполнен";
    public static final String PAYMENT_NOT_EQUALS="Сумма платежа не совпадает";

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
    public static final String MESSAGE_SMALL_INTERVAL = "Слишком маленький интервал запроса";
    //Send message
    public static final String SMS_CODE_MESSAGE = "Код: %s для входа в личный кабинет https://atc.express/";
    public static final String SMS_NEED_PAYMENT = "Уважаемый клиент!\n" +
            "Для получения экспресс-груза %s, приобретённого в %s, " +
            "необходимо оплатить таможенные пошлины. Оплата доступна на сайте %s в разделе заказы";
    public static final String SMS_NEED_DOCUMENT = "Уважаемый клиент!\n" +
            "Для получения экспресс-груза %s, приобретённого в %s, Вам необходимо предоставить документы и сведения на сайте %s.";

    //Email title
    public static final String EMAIL_TITLE_NEED_DOCUMENT = "Предоставление документов для получения экспресс-груза  %s,  приобретённого в %s";
    public static final String EMAIL_TITLE_NEED_PAYMENT = "Оплата таможенных пошлин по экспресс-грузу %s, приобретённого в %s";

    public static final String EMAIL_SEND_EXCEPTION = "Ошибка отправки почты на %s";
}

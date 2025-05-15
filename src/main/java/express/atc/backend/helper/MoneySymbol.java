package express.atc.backend.helper;

import express.atc.backend.model.MoneyModel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public class MoneySymbol {

    private final static Map<String, String> currency = new HashMap<>();

    static {
        currency.put("USD", "$");
        currency.put("EUR", "€");
        currency.put("RUB", "₽");
        currency.put("JPY", "¥");
        currency.put("CNY", "¥");
    }

    public static String getCharacter(String code) {
        return currency.getOrDefault(code, code);
    }

    public static String getStringMoney(MoneyModel model) {
        String symbolChar= getCharacter(model.getCurrency());
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator(' ');
        DecimalFormat dF = new DecimalFormat("#,###.00");
        return dF.format(model.getAmount() == null || model.getAmount() == 0 ? 0.0 : model.getAmount() / 100.0)+symbolChar;
    }
}

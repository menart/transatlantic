package express.atc.backend.integration.telegram.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String botUserName;

    @Override
    public void onUpdateReceived(Update update) {

    }

    public TelegramBot(
            @Value("${telegram.token}") String botToken,
            @Value("${telegram.name}") String botUserName) {
        super(botToken);
        this.botUserName = botUserName;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }
}

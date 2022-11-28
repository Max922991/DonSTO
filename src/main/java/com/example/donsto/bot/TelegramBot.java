package com.example.donsto.bot;

import com.example.donsto.config.BotConfig;
import com.example.donsto.model.Order;
import com.example.donsto.repositories.OrderRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    static final String ERROR_TEXT = "Error occurred: ";

    static final String HELP = "Вы можете удалить заказ по данному шаблону: " + "\n" +
            "/remove ID";
    static final String PATTERN = "Вводите Ваш заказ в строгом соответствии данному шаблону: " +
            "марка_авто-модель-год_выпуска-описание_работ-дата_приёма_авто";

    @Autowired
    private OrderRepo orderRepo;
    private final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "Начало работы"));
        commandList.add(new BotCommand("/get", "Получить все заказы"));
        commandList.add(new BotCommand("/help", HELP));
        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getChatId());
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (message.equals("/start")) {
                sendMessage(chatId, PATTERN);
            } else if (message.equals("/get")) {
                sendMessage(chatId, orderRepo.findAll().toString());
            } else if (message.startsWith("/remove")) {
                sendMessage(chatId, deleteOrder(chatId, message));
            } else if (message.equals("/help")) {
                sendMessage(chatId, HELP);
            } else {
               sendMessage(chatId, addOrder(chatId, message));
            }
        }
    }

    private String deleteOrder(long chatId, String message) {
        if (chatId != botConfig.getAdminId1() || chatId != botConfig.getAdminId2()) {
            return "У Вас нет доступа на удаление либо добавление заказов!";
        }
        String[] split = message.split(" ");
        orderRepo.deleteById(Long.valueOf(split[1]));

        return "Заказ удалён!";
    }

    private String addOrder(long chatId, String message) {
        if (chatId != botConfig.getAdminId1() || chatId != botConfig.getAdminId2()) {
            return "У Вас нет доступа на удаление либо добавление заказов!";
        }

        Order order = new Order();
        String[] strings = message.split("-");
        if(strings.length != 5) {
            return "Заказ набран не по шаблону " + "\n" + PATTERN;
        }

        order.setBrand(strings[0]);
        order.setModel(strings[1]);
        order.setYearOfRelease(strings[2]);
        order.setDescription(strings[3]);
        order.setArrivalDate(strings[4]);
        order.setDate(new Date(System.currentTimeMillis()));

        orderRepo.save(order);

        return "Заказ успешно добавлен";
    }

    private void sendMessage(long id, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(id));
        sendMessage.setText(textToSend);

        executeMessage(sendMessage);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }
}

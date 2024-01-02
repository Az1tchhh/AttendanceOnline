package com.example.automatedattendancesystemspring;

import com.example.automatedattendancesystemspring.config.BotConfig;
import com.example.automatedattendancesystemspring.service.AttendanceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot{
    private final BotConfig botConfig;
    private final AttendanceService attendanceService;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String result = "";
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    if (messageText.contains(":")){
                        String[] credentials = messageText.split(":");
                        String login = credentials[0];
                        String password = credentials[1];
                        sendMessage(chatId, "Marking attendance...");
                        try {
                            result = attendanceService.markAttendance(login, password);
                        } catch (InterruptedException e) {
                            sendMessage(chatId,"Something went wrong");
                        }
                        if(result == "Something went wrong"){
                            sendMessage(chatId, result);
                            break;
                        }
                        byte[] decodedScreen = Base64.getDecoder().decode(result);
                        ByteArrayInputStream bais = new ByteArrayInputStream(decodedScreen);

                        // Create a SendPhoto object with the chat ID and the InputStream
                        SendPhoto photo = new SendPhoto();
                        photo.setChatId(chatId);
                        photo.setPhoto(new InputFile(bais, "screenshot.png"));
                        try {
                            execute(photo);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        sendMessage(chatId, "Done");
                        break;
                    }
            }
        }
    }
    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
                "Please send your login and password in the format: login:password";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }
}
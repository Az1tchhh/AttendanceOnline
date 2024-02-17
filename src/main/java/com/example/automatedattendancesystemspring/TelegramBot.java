package com.example.automatedattendancesystemspring;

import com.example.automatedattendancesystemspring.config.BotConfig;
import com.example.automatedattendancesystemspring.models.Student;
import com.example.automatedattendancesystemspring.service.AttendanceService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot{
    private final BotConfig botConfig;
    private AttendanceService attendanceService;
    private Map<Long, Boolean> waitingForCredentials;
    private Map<Long, Boolean> waitingForLogin;
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
        boolean isScanning = false;
        String result = "";
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            System.out.println(update.getMessage().getMessageId());
            switch (messageText){
                case "/stop":
                    sendMessage(chatId, "Stopped");
                    waitingForCredentials.put(chatId, false);
                    waitingForLogin.put(chatId, false);
                    break;
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/attendanceinfo":
                    sendMessage(chatId, "Send your login...");
                    waitingForLogin.put(chatId, true);
                    break;
                case "/mark":
                    sendMessage(chatId, "Send your login and password in this format: login:password");
                    waitingForCredentials.put(chatId, true);
                    break;
                default:
                    if (waitingForLogin.get(chatId) != null && waitingForLogin.get(chatId)){
                        try{
                            sendMessage(chatId,"Providing information about marked attendances");
                            Student student = attendanceService.getStudent(messageText);
                            if(student != null)
                                sendMessage(chatId, student.toString());
                            else sendMessage(chatId, "Subscribe for the attendance now!");
                            waitingForLogin.put(chatId, false);
                        }catch (Exception e){

                            sendMessage(chatId, "Exception appeared, sorry(");
                            throw e;
                        }
                    }
                    if (waitingForCredentials.get(chatId) != null && waitingForCredentials.get(chatId)) {
                        if (messageText.contains(":")) {
                            String[] credentials = messageText.split(":");
                            String login = credentials[0];
                            String password = credentials[1];
                            sendMessage(chatId, "Marking attendance...");
                            sendMessage(chatId, "You can leave the rest to me");
                            isScanning = true;
                            try {
                                int iterations = 0;
                                while (isScanning){
                                    iterations++;
                                    String msg = update.getMessage().getText();
                                    System.out.println(iterations);
                                    if(iterations >= 3){
                                        sendMessage(chatId, "No attendances found, try again...");
                                        break;
                                    }
                                    result = attendanceService.markAttendance(login, password);
                                    if (result.equals("Something went wrong")) {
                                        sendMessage(chatId, result);
                                        break;
                                    }
                                    else if(result.equals("no attendance")){
                                        continue;
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
                                }

                            } catch (InterruptedException e) {
                                sendMessage(chatId, "Something went wrong");
                            }

                            sendMessage(chatId, "Done");
                            break;
                        } else sendMessage(chatId, "Incorrect format of data");

                        waitingForCredentials.put(chatId, false);
                    }
                    break;
            }
        }
    }
    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
                "If you want to sign attendance whenever it starts," + "\n" +
                "type /mark below" + "("+chatId+")";
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
//    public void sendPeriodicMessage(Long chatId) {
//        sendMessage(chatId, "Attendance is being scanned...");
//    }
//    @Scheduled(fixedRate = 300000) // 300,000 milliseconds = 5 minutes
//    public void sendPeriodicMessageToUsers() {
//        if(isScanning)
//            sendPeriodicMessage(chatIdCopy);
//    }
}

package test;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.*;
import java.util.HashSet;

public class SimpleBot extends TelegramLongPollingBot {
    private HashSet<String> UsersChat_ID = new HashSet<String>();

    public static void main(String[] args) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new SimpleBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "BrutalFootballBot";
    }

    @Override
    public String getBotToken() {
        return "token";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            if(message.getText().equals("/start")){
                UsersChat_ID.add(message.getChat().toString());
                sendMsg(message, "Привіт я бот Брутального футболу!");
                BotThread t = new BotThread();
                t.start();
            }
            else if (message.getText().equals("/help"))
                sendMsg(message, "Привет, я робот");

            else if (message.getText().equals("memas")){
                java.io.File file = new java.io.File("D:\\memasy\\5GXcToqMCW8.jpg");

                try{
                    FileInputStream in = new FileInputStream(file);
                    uploadFile(in, file.getName(), message.getChatId().toString());
                }
                catch (FileNotFoundException  e){
                    e.printStackTrace();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            else
                sendMsg(message, "Я не знаю что ответить на это");
        }
    }
    public HttpEntity uploadFile (FileInputStream f, String fileName, String chat_id)throws IOException{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("https://api.telegram.org/bot"+getBotToken()+"/sendPhoto");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("chat_id", chat_id, ContentType.TEXT_PLAIN);

// This attaches the file to the POST:
        builder.addBinaryBody(
                "photo",
                f,
                ContentType.APPLICATION_OCTET_STREAM,
                fileName
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();
        String result;
        if (responseEntity != null) {
            // A Simple JSON Response Read
            InputStream instream = responseEntity.getContent();
            result = convertStreamToString(instream);
            // now you have the string representation of the HTML request
            System.out.println("RESPONSE: " + result);
            instream.close();
        }
        return responseEntity;
    }
    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
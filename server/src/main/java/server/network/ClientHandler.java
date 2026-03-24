package server.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONObject;
import server.mapper.EmailMapper;
import server.mapper.UserMapper;
import server.network.request.GetEmailsRequest;
import server.network.request.GetUserRequest;
import server.network.response.GetEmailsResponse;
import server.network.response.GetUserResponse;
import server.services.EmailService;
import server.services.UserService;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    Socket socket;
    private BufferedReader sIn = null;
    private PrintWriter sOut = null;
    final private EmailService emailService;
    final private UserService userService;
    final private ObjectMapper jsonMapper = new ObjectMapper();

    ClientHandler(Socket socket, EmailService emailService, UserService userService) {
        this.socket = socket;
        this.emailService = emailService;
        this.userService = userService;
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        System.out.println("New Handler");
        try {
            sIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void run() {
        String message = "";
        while (true) {
            try {
                message = sIn.readLine();
                if (message == null)
                    continue;

                System.out.println("new message" + message);
                JSONObject msg = new JSONObject(message);
                String type = msg.getString("type");
                handleRequest(message, type);
            } catch (IOException i) {
                System.out.println(i.toString());
                return;
            }
        }
    }

    public void handleRequest(String jsonRequest, String type)
    {
        try{
            switch (type) {
                case "GetEmails":
                    getEmailsHandler(jsonMapper.readValue(jsonRequest, GetEmailsRequest.class));
                    break;
                case "GetUser":
                    getUserHandler(jsonMapper.readValue(jsonRequest, GetUserRequest.class));
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
    public void getEmailsHandler(GetEmailsRequest request) {
        System.out.println("getEmails Handler");
        var emails = emailService.getUserEmails(request.getUserId());

//            var data = new JSONArray();
//            for (var email : emails) {
//                var emailJson = new JSONObject();
//                emailJson.put("id", email.getEmailId().toString());
//                emailJson.put("from", email.getEmailId().toString());
//                emailJson.put("to", email.receiver_id.toString());
//                emailJson.put("subject", email.subject);
//                emailJson.put("body", email.body);
//                data.put(emailJson);
//            }
//            sOut.println("ok");
        var emailDtos = emails.stream().map(EmailMapper::toDTO).collect(Collectors.
                toCollection(ArrayList::new));

        try {
            var json = jsonMapper.writeValueAsString(
                    new GetEmailsResponse(request.getRequestId(), "success", request.getUserId(), emailDtos));
            sOut.println(json);
            System.out.println("response sent");
        } catch (JsonProcessingException e) {
            System.out.println("json mapper exception " + e.toString());
        }

    }

    public void getUserHandler(GetUserRequest request) {
        var existing = userService.getUserByUserId(request.getUserId());

        try {
            var json = jsonMapper.writeValueAsString(
                    existing.isPresent() ? new GetUserResponse(request.getRequestId(), "success", UserMapper.toDTO(existing.get())) :
                            new GetUserResponse(request.getRequestId(),"not found", null)
            );
            sOut.println(json);
            System.out.println("response sent");
        } catch (JsonProcessingException e) {
            System.out.println("json mapper exception " + e.toString());
        }

    }
}

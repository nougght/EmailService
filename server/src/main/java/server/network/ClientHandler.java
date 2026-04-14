package server.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import server.mapper.EmailMapper;
import server.mapper.UserMapper;
import server.model.User;
import server.network.message.Message;
import server.network.message.MessageDeserializer;
import server.network.request.*;
import server.network.response.*;
import server.services.AuthService;
import server.services.EmailService;
import server.services.UserService;


import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    Socket socket;
    private BufferedReader sIn = null;
    private PrintWriter sOut = null;
    final private AuthService authService;
    final private EmailService emailService;
    final private UserService userService;
    final private ObjectMapper jsonMapper = new ObjectMapper();
    final private MessageDeserializer deserializer = new MessageDeserializer();

    final private HashMap<String, Object> handlers = new HashMap<>(Map.ofEntries(

    ));

    ClientHandler(Socket socket, AuthService authService, EmailService emailService, UserService userService) {
        this.socket = socket;
        this.authService = authService;
        this.emailService = emailService;
        this.userService = userService;
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Message.class, new MessageDeserializer());
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.registerModule(module);
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

//                System.out.println("new message" + message);
//                JSONObject msg = new JSONObject(message);
//                String type = msg.getString("type");

                Message msg = jsonMapper.readValue(message, Message.class);

                if (msg instanceof Request) {
                    handleRequest((Request) msg);
                }

            } catch (IOException i) {
                System.out.println(i.toString());
                return;
            }
        }
    }

    public void handleRequest(Request request) {
        try {

            switch (request.getType()) {
                case "Registration":
                    registrationHandler((RegistrationRequest) request);
                    break;
                case "Login":
                    loginHandler((LoginRequest) request);
                    break;
                case "Refresh":
                    refreshHandler((RefreshRequest) request);
                    break;
                case "Logout":
                    logoutHandler((LogoutRequest) request);
                    break;
                case "GetEmails":
                    getEmailsHandler((GetEmailsRequest) request);
                    break;
                case "GetUser":
                    getUserHandler((GetUserRequest) request);
                    break;
            }
        } catch (Exception e) {
            System.out.println("ClientHandler" + socket.getInetAddress() + " " + e.toString());
        }

    }

    private void logoutHandler(LogoutRequest request) {
        UUID userId = authService.verifyAccessToken(request.getAccessToken());
        if (userId == null)
            return;

        authService.logout(userId);
        try {
            var json = jsonMapper.writeValueAsString(new LogoutResponse(
                    request.getRequestId(),
                    "success"
            ));
            sOut.println(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void getEmailsHandler(GetEmailsRequest request) {
        System.out.println("getEmails Handler");
//        проверка токена доступа
        UUID userId = authService.verifyAccessToken(request.getAccessToken());
        if (userId == null)
            return;
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
//        проверка токена доступа
        UUID userId = authService.verifyAccessToken(request.getAccessToken());
        if (userId == null)
            return;

        var existing = userService.getUserByUserId(request.getUserId());

        try {
            var json = jsonMapper.writeValueAsString(
                    existing.isPresent() ? new GetUserResponse(request.getRequestId(), "success", UserMapper.toDTO(existing.get())) :
                            new GetUserResponse(request.getRequestId(), "not found", null)
            );
            sOut.println(json);
            System.out.println("response sent");
        } catch (JsonProcessingException e) {
            System.out.println("json mapper exception " + e.toString());
        }

    }


    public void registrationHandler(RegistrationRequest request) {
        var result = authService.register(request.getUsername(), request.getPassword());
        User user = result.getValue0();
        String status = result.getValue1();

        try {

            var response = new RegistrationResponse(
                    request.getRequestId(),
                    status,
                    UserMapper.toDTO(user),
                    "",
                    ""
            );

            if (Objects.equals(status, "success")) {
                String refreshToken = UUID.randomUUID().toString();
                authService.addRefreshToken(UUID.randomUUID().toString(), user.getUserId());
                response.setAccessToken(authService.genAccessToken(user.getUserId()));
                response.setRefreshToken(refreshToken);
            }
            var json = jsonMapper.writeValueAsString(response);
            sOut.println(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loginHandler(LoginRequest request) {
        var result = authService.login(request.getUsername(), request.getPassword());
        User user = result.getValue0();
        String status = result.getValue1();

        try {
            var response = new LoginResponse(
                    request.getRequestId(),
                    status,
                    UserMapper.toDTO(user),
                    "",
                    ""
            );
            if (Objects.equals(status, "success")) {
                String refreshToken = UUID.randomUUID().toString();
                authService.addRefreshToken(refreshToken, user.getUserId());
                response.setAccessToken(authService.genAccessToken(user.getUserId()));
                response.setRefreshToken(refreshToken);
            }
            var json = jsonMapper.writeValueAsString(response);
            sOut.println(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshHandler(RefreshRequest request) {
        var isValid = authService.checkRefreshToken(request.getRefreshToken(), request.getUserId());
        var optionalUser = userService.getUserByUserId(request.getUserId());
        var status = "";
        User user = null;
        String accessToken = null;
        if (!isValid) {
            status = "invalid refresh token";
        } else if (optionalUser.isEmpty()){
            status="not found";
        } else {
            user = optionalUser.get();
            status="success";
            accessToken = authService.genAccessToken(request.getUserId());
        }
        try {
            var response = new RefreshResponse(request.getRequestId(),
                    status,
                    UserMapper.toDTO(user),
                    accessToken
            );
            var json = jsonMapper.writeValueAsString(response);
            sOut.println(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

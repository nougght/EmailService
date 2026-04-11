package client.network;

import client.dto.EmailDTO;
import client.dto.UserDTO;
import client.model.AuthResult;
import client.model.Email;
import client.network.request.*;
import client.network.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

// сетевое взимодействие
public class TcpClient extends Thread {
    private String sHost;
    private int sPort;

    private TcpListener serverListener;
    private SSLSocket socket;
    private BufferedReader sIn = null;
    private PrintWriter sOut = null;

    private String accessToken;

    final private ObjectMapper jsonMapper = new ObjectMapper();

    final private BlockingQueue requests = new LinkedBlockingQueue<String>();
    final private ConcurrentHashMap<UUID, CompletableFuture<Response>> pendingResponses = new ConcurrentHashMap<>();

    public TcpClient(String host, int port) {
        sHost = host;
        sPort = port;
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void run() {
        super.run();
        System.out.println("Connecting...");
        try {
            KeyStore ts = KeyStore.getInstance("JKS");
            InputStream in = getClass().getClassLoader().getResourceAsStream("client/truststore.jks");
            System.out.println(in);
            ts.load(in, "49Kst0rE/701".toCharArray());
            Enumeration<String> aliases = ts.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                System.out.println(alias + " -> " + ts.getCertificate(alias).getPublicKey());
            }
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);

            SSLSocketFactory ssf = ctx.getSocketFactory();
            socket = (SSLSocket) ssf.createSocket(sHost, sPort);

            System.out.print("Connected: ");
            System.out.println(socket.getInetAddress());
            sIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            serverListener = new TcpListener(socket, pendingResponses);

            // запуск потока на чтение запросов с сервера
            serverListener.start();

            while (true) {
                // ожидание нового запроса из очереди
                var request = requests.take();
                System.out.println("sent new request " + request);
                // отправка запроса на сервер
                sOut.println(request);

            }
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }



    public CompletableFuture<AuthResult> requestRegistration(String username, String password){
        try {
            var request = new RegistrationRequest(username, password);
            String jsonRequest = jsonMapper.writeValueAsString(request);
            CompletableFuture<Response> future = new CompletableFuture<>();
            pendingResponses.put(request.getRequestId(), future);
            requests.put(jsonRequest);
            return future.thenApply(resp -> {
                var response = (RegistrationResponse) resp;
                return new AuthResult(response.getStatus(),response.getUser(), response.getAccessToken(), response.getRefreshToken());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<AuthResult> requestLogin(String username, String password){
        try {
            var request = new LoginRequest(username, password);

            String jsonRequest = jsonMapper.writeValueAsString(request);
            CompletableFuture<Response> future = new CompletableFuture<>();
            pendingResponses.put(request.getRequestId(), future);
            requests.put(jsonRequest);
            return future.thenApply(resp -> {
                var response = (LoginResponse) resp;
                return new AuthResult(response.getStatus(),response.getUser(), response.getAccessToken(), response.getRefreshToken());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<String> requestLogout(UUID userId){
        try {
            var request = new LogoutRequest();
            request.setAccessToken(accessToken);

            var jsonRequest = jsonMapper.writeValueAsString(request);
            CompletableFuture<Response> future = new CompletableFuture<>();

            pendingResponses.put(request.getRequestId(), future);
            requests.add(jsonRequest);
            return future.thenApply(resp -> {
                var response = (LogoutResponse) resp;
                return response.getStatus();
            });
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Optional<UserDTO>> requestUserByUserIdAsync(UUID userId) {
        try {

            var request = new GetUserRequest(
                    userId
            );

            request.setAccessToken(accessToken);

            String jsonRequest = jsonMapper.writeValueAsString(request);
            CompletableFuture<Response> future = new CompletableFuture<>();
            // добавляем в таблицу ожидаемых ответов
            System.out.println("added request " + request.getRequestId() + " to queue");
            pendingResponses.put(request.getRequestId(), future);
            // добавляем в очередь запросов
            requests.put(jsonRequest);
            return future
                    .thenApply(resp -> {
                        var response = (GetUserResponse) resp;
                        System.out.println("handling response " + response.getRequestId());
                        if (response.getStatus().equals("success")) {
                            return Optional.ofNullable(response.getUserDTO());
                        } else {
                            return Optional.<UserDTO>empty();
                        }
                    })
                    .exceptionally(ex -> {
                        throw new RuntimeException(ex.toString());
                    });
//            sOut.println(jsonRequest);
//            String response = "";
////                    sIn.readLine();
////            System.out.println("response: " + response);
////            GetUserResponse getUserResponse = jsonMapper.readValue(response, GetUserResponse.class);
////            if (getUserResponse.getStatus().equals("success")) {
////                return Optional.ofNullable(getUserResponse.getUserDTO());
////            } else {
//            return Optional.empty();
////            }
//
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<ArrayList<EmailDTO>> requestAllUserEmails(UUID userId) {
        try {
            var request = new GetEmailsRequest(userId);

            request.setAccessToken(accessToken);

            String jsonRequest = jsonMapper.writeValueAsString(request);
            CompletableFuture<Response> future = new CompletableFuture<>();

            pendingResponses.put(request.getRequestId(), future);
            requests.put(jsonRequest);
            return future.thenApply(resp -> {
                var response = (GetEmailsResponse) resp;
                if (response.getStatus().equals("success")) {
                    return response.getEmails();
                } else {
                    return new ArrayList<EmailDTO>();
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

//    public CompletableFuture<Optional<Email>> requestEmailByEmailId(UUID emailId){
//        try {
//            var request = new
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
    public CompletableFuture<AuthResult> requestAutoAuth(String refreshToken, UUID userId) {
        try {
            var request = new RefreshRequest(userId, refreshToken);

            String jsonRequest = jsonMapper.writeValueAsString(request);

            CompletableFuture<Response> future = new CompletableFuture<>();

            pendingResponses.put(request.getRequestId(), future);
            requests.put(jsonRequest);
            return future.thenApply(resp -> {
                var response = (RefreshResponse) resp;
                return new AuthResult(response.getStatus(), response.getUser(), response.getAccessToken(), null);
            });
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}

//    public ArrayList<EmailDTO> requestEmails()


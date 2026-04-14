package client.network.message;

import client.network.request.*;
import client.network.response.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class MessageDeserializer extends StdDeserializer<Message> {
    public MessageDeserializer() { super(Message.class); }

    @Override
    public Message deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectNode node = p.getCodec().readTree(p);
        String kind = node.get("kind").asText();
        String type = node.get("type").asText();

        return switch (kind + ":" + type) {
            case "REQUEST:Registration"  -> p.getCodec().treeToValue(node, RegistrationRequest.class);
            case "REQUEST:Refresh"  -> p.getCodec().treeToValue(node, RefreshRequest.class);
            case "REQUEST:Login"    -> p.getCodec().treeToValue(node, LoginRequest.class);
            case "REQUEST:Logout"  -> p.getCodec().treeToValue(node, LogoutRequest.class);
            case "REQUEST:GetUser"  -> p.getCodec().treeToValue(node, GetUserRequest.class);
            case "REQUEST:GetEmails"  -> p.getCodec().treeToValue(node, GetEmailsRequest.class);
            case "RESPONSE:Registration"   -> p.getCodec().treeToValue(node, RegistrationResponse.class);
            case "RESPONSE:Refresh" -> p.getCodec().treeToValue(node, RefreshResponse.class);
            case "RESPONSE:Login"   -> p.getCodec().treeToValue(node, LoginResponse.class);
            case "RESPONSE:Logout"   -> p.getCodec().treeToValue(node, LogoutResponse.class);
            case "RESPONSE:GetUser"   -> p.getCodec().treeToValue(node, GetUserResponse.class);
            case "RESPONSE:GetEmails"   -> p.getCodec().treeToValue(node, GetEmailsResponse.class);
            default -> throw new JsonParseException(p, "Unknown type: " + kind + ":" + type);
        };
    }
}

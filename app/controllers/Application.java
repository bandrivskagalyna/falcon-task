package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    //temporary stub for Redis pub/sub
    private static Map<UUID,String> messagesQueue = new HashMap<>();

    public static Result index() {
        return ok(index.render("Your new application is ready ."));
    }

    public static Result getAllMessages() {
        return ok(messagesQueue.values().toArray().toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result newMessage() {
      JsonNode json = request().body().asJson();
      String messageText = json.findPath("messageText").textValue();
      if(messageText == null) {
        return badRequest("Missing parameter [messageText]");
      } 
      messagesQueue.put(UUID.randomUUID(), messageText);
        return ok();
    }
}

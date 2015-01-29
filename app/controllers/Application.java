package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.*;
import services.RedisService;
import views.html.*;

public class Application extends Controller {

	private static RedisService redisDB = new RedisService();

	private static List<WebSocket.Out<String>> channels = new ArrayList<>();

	// temporary stub for Redis pub/sub
	private static Map<UUID, String> messagesQueue = new HashMap<>();

	public static Result index() {
        return ok(index.render("Your new application is ready ."));
	}

	public static Result deliveredMessage() {
		return ok(realtimeMessages.render());
	}

	public static Result getAllMessages() {
		List<Object> messages = redisDB.getAllValues();
		return ok(allMessages.render(messages));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result newMessage() {
		JsonNode json = request().body().asJson();
		String messageText = json.findPath("messageText").textValue();
		if (messageText == null) {
			return badRequest("Missing parameter [messageText]");
		}
		messagesQueue.put(UUID.randomUUID(), messageText);
		for (WebSocket.Out<String> channel : channels) {
			channel.write(messageText);
		}
		return ok();
	}

	public static WebSocket<String> websocket() {

		return WebSocket.whenReady((in, out) -> {
			channels.add(out);

			in.onClose(() -> System.out.println("Disconnected"));

		});
	}

}

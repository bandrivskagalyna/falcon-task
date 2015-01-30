package controllers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.mvc.*;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import services.database.RedisService;
import services.queue.Publisher;
import services.queue.Subscriber;
import services.utils.ConfigurationUtils;
import views.html.*;

public class Application extends Controller {

	private static final String PUB_SUB_CHANNEL = "pubSubChannel";
	private static Publisher publisher;
	private static JedisPool jedisPool;
	private static List<WebSocket.Out<String>> channels;

	static {

		jedisPool = new JedisPool(new JedisPoolConfig(),
				ConfigurationUtils.REDIS_HOST, ConfigurationUtils.REDIS_PORT);
		channels = new ArrayList<>();

		// initialize subscriber
		new Thread(() -> {
			try {
				jedisPool.getResource().subscribe(new Subscriber(),
						PUB_SUB_CHANNEL);
			} catch (Exception e) {
				System.out.println("Error subscribing to Pub/Sub channel.");
			}
		}).start();

		// initialize publisher
		publisher = new Publisher(jedisPool.getResource(), PUB_SUB_CHANNEL);
	}

	public static Result deliveredMessages() {
		return ok(deliveredMessages.render());
	}

	public static Result allMessages() {
		List<Object> messages = RedisService.getInstance().getAllValues();

		return ok(Json.toJson(messages));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result newMessage() {
		JsonNode json = request().body().asJson();
		JsonNode messageNode = json.findPath("messageText");

		if (messageNode == null || messageNode.textValue() == null) {
			return badRequest("Missing parameter [messageText]");
		}

		String messageText = json.findPath("messageText").textValue();

		for (WebSocket.Out<String> channel : channels) {
			// write message to show on deliveredMessages html page
			channel.write(messageText);
		}

		publisher.publishMessage(json);

		return ok();
	}

	public static WebSocket<String> webSocket() {

		return WebSocket.whenReady((in, out) -> {
			channels.add(out);

			in.onClose(() -> System.out.println("Disconnected"));
		});
	}

}
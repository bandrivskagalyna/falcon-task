package controllers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import services.database.RedisService;
import services.queue.Publisher;
import services.queue.Subscriber;
import views.html.*;

public class Application extends Controller {

	private static final String PUB_SUB_CHANNEL = "pubSubChannel";
	private static Publisher publisher;
	private static JedisPool jedisPool = new JedisPool(new JedisPoolConfig(),
			"localhost");
	private static List<WebSocket.Out<String>> channels = new ArrayList<>();

	static {
		// initialize subscriber
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					jedisPool.getResource().subscribe(new Subscriber(),
							PUB_SUB_CHANNEL);
				} catch (Exception e) {
				}
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
		return ok(allMessages.render(messages));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result newMessage() {
		JsonNode json = request().body().asJson();
		String messageText = json.findPath("messageText").textValue();
		if (messageText == null) {
			return badRequest("Missing parameter [messageText]");
		}

		publisher.publishMessage(messageText);

		for (WebSocket.Out<String> channel : channels) {
			// write message to show on deliveredMessages html page
			channel.write(messageText);
		}
		return ok();
	}

	public static WebSocket<String> webSocket() {

		return WebSocket.whenReady((in, out) -> {
			channels.add(out);

			in.onClose(() -> System.out.println("Disconnected"));

		});
	}

}
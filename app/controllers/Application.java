package controllers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import services.database.RedisService;
import services.queue.Subscriber;
import views.html.*;

public class Application extends Controller {

	public static final String PUB_SUB_CHANNEL = "pubSubChannel";
	public static JedisPool jedisPool = new JedisPool(new JedisPoolConfig(),
			"localhost");

	private static List<WebSocket.Out<String>> channels = new ArrayList<>();

	public static Result index() {
		return ok(index.render("Your new application is ready ."));
	}

	public static Result deliveredMessage() {

		// Add subscriber
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Jedis j = jedisPool.getResource();
					j.subscribe(new Subscriber(), PUB_SUB_CHANNEL);
				} catch (Exception e) {
				}
			}
		}).start();

		return ok(realtimeMessages.render());
	}

	public static Result getAllMessages() {
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
		// messagesQueue.put(UUID.randomUUID(), messageText);
		for (WebSocket.Out<String> channel : channels) {
			// write message to show on deliveredMessages html page
			channel.write(messageText);

			Jedis j = jedisPool.getResource();
			try {
				// All messages are pushed through the pub/sub channel
				j.publish(PUB_SUB_CHANNEL, messageText);
			} finally {
				jedisPool.returnResource(j);
			}
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

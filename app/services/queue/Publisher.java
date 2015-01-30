package services.queue;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import redis.clients.jedis.Jedis;

public class Publisher {

	private final Jedis publisherJedis;

	private final String channel;

	public Publisher(Jedis publisherJedis, String channel) {
		this.publisherJedis = publisherJedis;
		this.channel = channel;
	}

	public void publishMessage(JsonNode messageJson) {
		publisherJedis.publish(channel, Json.stringify(messageJson));
	}
}

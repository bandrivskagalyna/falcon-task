package services.queue;

import redis.clients.jedis.Jedis;

public class Publisher {

	private final Jedis publisherJedis;

	private final String channel;

	public Publisher(Jedis publisherJedis, String channel) {
		this.publisherJedis = publisherJedis;
		this.channel = channel;
	}

	public void publishMessage(String messageText) {
		publisherJedis.publish(channel, messageText);
	}

}

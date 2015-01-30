package services.queue;

import redis.clients.jedis.JedisPubSub;
import services.database.RedisService;

public class Subscriber extends JedisPubSub {
	
	@Override
	public void onMessage(String channel, String messageBody) {
		// Process messages from the pub/sub channel
		RedisService.getInstance().addValue(messageBody);
	}

	@Override
	public void onPMessage(String arg0, String arg1, String arg2) {
	}

	@Override
	public void onPSubscribe(String arg0, int arg1) {
	}

	@Override
	public void onPUnsubscribe(String arg0, int arg1) {
	}

	@Override
	public void onSubscribe(String arg0, int arg1) {
	}

	@Override
	public void onUnsubscribe(String arg0, int arg1) {
	}
}
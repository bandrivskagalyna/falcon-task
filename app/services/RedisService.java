package services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import redis.clients.jedis.Jedis;

public class RedisService {
	private static Jedis jedis = new Jedis("localhost");

	public void addValue(String value) {
		jedis.set(UUID.randomUUID().toString(), value);
	}

	public List<Object> getAllValues() {
		List<Object> values=new ArrayList<Object>();
		Set<String> keys = jedis.keys("*");
		 Iterator<String> it = keys.iterator();
		    while (it.hasNext()) {
		        String key = it.next();
		        values.add(jedis.get(key));
		    }
		return values;
	}
}

package services.utils;

import com.typesafe.config.ConfigFactory;

public final class ConfigurationUtils {	

	public static final String REDIS_HOST = ConfigFactory.load().getString("redis.host");
	
	public static final int REDIS_PORT = ConfigFactory.load().getInt("redis.port");
	
	public static final Long REDIS_TIMEOUT = ConfigFactory.load().getLong("redis.timeout");

}

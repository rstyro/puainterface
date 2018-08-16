package top.lrshuai.pua.service;

import java.util.Map;

public interface CacheService {

	public int cacheUserNickName();
	public int about();
	public Map<String, Object>  getAbout();
}

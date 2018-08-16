package top.lrshuai.pua.service.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import top.lrshuai.pua.dao.CacheDao;
import top.lrshuai.pua.service.CacheService;
import top.lrshuai.pua.util.ParameterMap;

@Service
public class CacheServiceImpl implements CacheService{

	private Logger log = Logger.getLogger(getClass());
	
	@Autowired
	private CacheDao cacheDao;
	
	@Autowired
	private RedisTemplate<String, String> redis;
	
	@Autowired
	private RedisTemplate<String, Object> oredis;

	@Override
	public int cacheUserNickName() {
		try {
			List<ParameterMap> users = cacheDao.getUserList(null);
			if(users!= null && users.size() > 0){
				for(ParameterMap user:users){
					String userId = user.getString("user_id");
					String nickName = user.getString("nick_name");
					
					//缓存用户的id与昵称
					redis.opsForValue().set(userId, nickName);
				}
			}
			return 1;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}
	
	@Override
	public int about() {
		try {
			ParameterMap pm = new ParameterMap();
			pm.put("type", "me");
			List<ParameterMap> meAbout = cacheDao.getAbout(pm);
			if(meAbout!= null && meAbout.size() > 0){
				List<Map<String,Object>> meList = new ArrayList<>();
				for(ParameterMap about:meAbout){
					Map<String,Object> map = new HashMap<>();
					String content = about.getString("text");
					map.put("text", content);
					meList.add(map);
				}
				//缓存用户的id与昵称
				oredis.opsForValue().set("meList", meList);
			}
			pm.put("type", "program");
			List<ParameterMap> programAbout = cacheDao.getAbout(pm);
			if(programAbout!= null && programAbout.size() > 0){
				List<Map<String,Object>> programList = new ArrayList<>();
				for(ParameterMap about:programAbout){
					Map<String,Object> map = new HashMap<>();
					String content = about.getString("text");
					map.put("text", content);
					programList.add(map);
				}
				//缓存用户的id与昵称
				oredis.opsForValue().set("programList", programList);
			}
			return 1;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public Map<String, Object> getAbout() {
		Map<String,Object> map = new HashMap<>();
		try {
			List<Map<String,Object>> meList = (List<Map<String, Object>>) oredis.opsForValue().get("meList");
			List<Map<String,Object>> programList = (List<Map<String, Object>>) oredis.opsForValue().get("programList");
			map.put("me", meList);
			map.put("program", programList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}
}

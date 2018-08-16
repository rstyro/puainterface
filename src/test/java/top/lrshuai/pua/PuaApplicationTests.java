package top.lrshuai.pua;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PuaApplicationTests {

	@Autowired
	private RedisTemplate<String, List<Map<String,Object>>> rm;
	
	
	@Test
	public void test2() throws Exception{
		List<Map<String,Object>> ms = new ArrayList<>();
		Map<String,Object> map = new HashMap<>();
		map.put("name", "rs");
		map.put("age", 20);
		
		Map<String,Object> map1 = new HashMap<>();
		map1.put("name", "rs1");
		map1.put("age", 21);
		
		Map<String,Object> map2 = new HashMap<>();
		map2.put("name", "rs2");
		map2.put("age", 22);
		
		ms.add(map);
		ms.add(map1);
		ms.add(map2);
		rm.opsForValue().set("key_ml", ms);
		System.out.println("放入缓存》。。。。。。。。。。。。。。。。。。。");
		System.out.println("=============================");
		List<Map<String,Object>> mls = rm.opsForValue().get("key_ml");
		System.out.println("mls="+mls);
	}

}

package top.lrshuai.pua.task;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import top.lrshuai.pua.service.CacheService;


@Component
public class InitCache {
	
	@Autowired
	private CacheService cacheService;

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 初始化方法
	 */
	@PostConstruct
	public void init() {
		try {
			cacheService.cacheUserNickName();
			cacheService.about();
			log.info("初始化缓存成功");
		} catch (Exception e) {
			log.error("初始化缓存出错,可能redis服务没开启", e);
		}
	}

}

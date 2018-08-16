package top.lrshuai.pua.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.lrshuai.pua.controller.base.BaseController;
import top.lrshuai.pua.service.CacheService;

/**
 * 缓存控制器
 * @author rstyro
 *
 */
@RestController
@RequestMapping("/cache")
public class CacheController extends BaseController{

	@Autowired
	private CacheService cacheService;
	
	@GetMapping(path="/reloadNickName")
	public Object cacheUserList(){
		return cacheService.cacheUserNickName();
	}
	
	@GetMapping(path="/about")
	public Object about(){
		return cacheService.about();
	}
}

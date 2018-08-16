package top.lrshuai.pua.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import top.lrshuai.pua.controller.base.BaseController;

@Controller
public class PageController extends BaseController{
	
	@GetMapping(path={"/","/index"})
	public String index(){
		log.info("comment in index ip:"+this.getIpAddr());
		return "index";
	}
	
	@GetMapping(path="/pua/{pageName}")
	public String pageName(@PathVariable("pageName") String pageName){
		
		return "pua/"+pageName;
	}
	
}

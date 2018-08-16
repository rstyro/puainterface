package top.lrshuai.pua.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import top.lrshuai.pua.controller.base.BaseController;
import top.lrshuai.pua.service.SpeechcraftService;

@Controller
@RequestMapping("/speechcraft")
public class SpeechcraftController extends BaseController{
	
	@Autowired
	private SpeechcraftService speechcraftService;
	
	
	/**
	 * 所有数据
	 * @return
	 */
	@GetMapping(path="/list")
	@ResponseBody
	public Object getSpeechcraftList(){
		return speechcraftService.getSpeechraftList(getParameterMap(),getSession());
	}
	
	/**
	 * 收藏
	 * @return
	 */
	@GetMapping(path="/getUserCollectList")
	@ResponseBody
	public Object getUserCollectList(){
		return speechcraftService.getUserCollectList(getParameterMap(),getSession());
	}
	
	
	@PostMapping(path="/update")
	@ResponseBody
	public Object updateSpeechcraft(){
		return speechcraftService.update(getParameterMap(),getSession());
	}
}

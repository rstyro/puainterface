package top.lrshuai.pua.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import top.lrshuai.pua.controller.base.BaseController;
import top.lrshuai.pua.service.PublicService;

@Controller
@RequestMapping("/public")
public class PublicController extends BaseController{
	
	@Autowired
	private PublicService publicService;
	
	/**
	 * 点赞
	 * @return
	 */
	@PostMapping(path="/praise")
	@ResponseBody
	public Object praise(){
		return publicService.savePraise(this.getParameterMap(),getSession());
	}
	
	
	/**
	 * 收藏
	 * @return
	 */
	@PostMapping(path="/collect")
	@ResponseBody
	public Object collect(){
		return publicService.saveCollect(this.getParameterMap(),getSession());
	}
	
	
	/**
	 * 反馈
	 * @return
	 */
	@PostMapping(path="/feedback")
	@ResponseBody
	public Object feedback(){
		return publicService.saveFeedback(this.getParameterMap(),getSession());
	}
	
	
	/**
	 * 用户投稿
	 * @return
	 */
	@PostMapping(path="/contribute")
	@ResponseBody
	public Object contribute(){
		return publicService.saveContribute(this.getParameterMap(),getSession());
	}
	
	/**
	 * 获取系统消息列表
	 * @return
	 */
	@GetMapping(path="/getSysMsgList")
	@ResponseBody
	public Object getSysMsgList(){
		return publicService.getSysMsgList(this.getParameterMap(),getSession());
	}
	
	
	/**
	 * 发布系统消息
	 * @return
	 */
	@PostMapping(path="/saveSystemMsg")
	@ResponseBody
	public Object saveSystemMsg(){
		return publicService.saveSystemMsg(this.getParameterMap(),getSession());
	}
	
	
	/**
	 * 标记系统消息为已读
	 * @return
	 */
	@PostMapping(path="/tabSysMsgRead")
	@ResponseBody
	public Object tabSysMsgRead(){
		return publicService.tabSysMsgRead(this.getParameterMap(),getSession());
	}
	
	/**
	 * 删除已读的消息列表
	 * @return
	 */
	@PostMapping(path="/delTabSysMsgRead")
	@ResponseBody
	public Object delTabSysMsgRead(){
		return publicService.delTabSysMsgRead(this.getParameterMap(),getSession());
	}
	
	@GetMapping(path="/about")
	@ResponseBody
	public Object about(){
		return publicService.getAboutList();
	}
}

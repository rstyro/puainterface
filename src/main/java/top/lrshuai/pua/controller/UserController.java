package top.lrshuai.pua.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import top.lrshuai.pua.controller.base.BaseController;
import top.lrshuai.pua.service.UserService;
import top.lrshuai.pua.util.ParameterMap;

/**
 * 
 * @author tyro
 * 用户操作类
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController{
	
	@Autowired
	private UserService userService;

	/**
	 * 登录
	 * @return
	 */
	@PostMapping(path="/login")
	@ResponseBody
	public Object login(){
		ParameterMap pm = getParameterMap();
		pm.put("ip", this.getIpAddr());
		return userService.login(pm, getSession());
	}
	
	/**
	 * 自动登陆
	 * @return
	 */
	@PostMapping(path="/autoLogin")
	@ResponseBody
	public Object autoLogin(){
		ParameterMap pm = getParameterMap();
		pm.put("ip", this.getIpAddr());
		return userService.autoLogin(pm, getSession());
	}
	
	/**
	 * 修改资料
	 * @return
	 */
	@PostMapping(path="/updateUserInfo")
	@ResponseBody
	public Object updateUserInfo(){
		ParameterMap pm = getParameterMap();
		pm.put("ip", this.getIpAddr());
		return userService.updateUserInfo(pm, getSession());
	}
	
	/**
	 * 上传图片
	 * @return
	 */
	@PostMapping(path="/uplaodImg")
	@ResponseBody
	public Object uplaodImg(@RequestParam(value = "file", required = false) MultipartFile file){
		return userService.uploadImg(getSession(),file);
	}
	
}

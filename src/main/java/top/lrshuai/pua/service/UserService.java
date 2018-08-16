package top.lrshuai.pua.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;

import top.lrshuai.pua.util.ParameterMap;

public interface UserService {
	
	public Map<String,Object> login(ParameterMap pm, HttpSession session);
	public Map<String,Object> autoLogin(ParameterMap pm, HttpSession session);
	public Map<String,Object> updateUserInfo(ParameterMap pm, HttpSession session);
	public Object uploadImg( HttpSession session,MultipartFile file);
	
}

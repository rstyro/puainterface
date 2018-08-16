package top.lrshuai.pua.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import top.lrshuai.pua.util.ParameterMap;

public interface PublicService {
	
	public Map<String,Object> savePraise(ParameterMap pm,HttpSession session); 
	
	public Map<String,Object> saveCollect(ParameterMap pm,HttpSession session);
	
	public Map<String,Object> saveFeedback(ParameterMap pm,HttpSession session); 
	public Map<String,Object> saveContribute(ParameterMap pm,HttpSession session); 
	public Map<String,Object> saveSystemMsg(ParameterMap pm,HttpSession session); 
	public Map<String,Object> getSysMsgList(ParameterMap pm,HttpSession session); 
	public Map<String,Object> tabSysMsgRead(ParameterMap pm,HttpSession session); 
	public Map<String,Object> delTabSysMsgRead(ParameterMap pm,HttpSession session); 
	public Map<String,Object> getAboutList(); 
}

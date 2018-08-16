package top.lrshuai.pua.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import top.lrshuai.pua.util.ParameterMap;

public interface SpeechcraftService {
	
	public Map<String,Object> save(ParameterMap pm,HttpSession session);
	public Map<String,Object> find(ParameterMap pm);
	public Map<String,Object> del(ParameterMap pm,HttpSession session);
	public Map<String,Object> update(ParameterMap pm,HttpSession session);
	public Map<String,Object> getSpeechraftList(ParameterMap pm, HttpSession session);
	public Map<String,Object> getUserCollectList(ParameterMap pm, HttpSession session);
}

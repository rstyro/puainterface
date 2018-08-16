package top.lrshuai.pua.dao;

import java.util.List;

import top.lrshuai.pua.util.ParameterMap;

public interface CacheDao {
	/**
	 * 获取所有用户的id 与昵称
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getUserList(ParameterMap pm);
	
	public List<ParameterMap> getAbout(ParameterMap pm);
}

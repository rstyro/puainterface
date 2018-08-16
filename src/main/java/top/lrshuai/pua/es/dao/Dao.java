package top.lrshuai.pua.es.dao;

import java.util.Map;

import top.lrshuai.pua.plugin.Page;
import top.lrshuai.pua.util.ParameterMap;

public interface Dao {
	public String save(ParameterMap pm,String index,String type);
	public int update(ParameterMap pm,String index,String type);
	public int updateByScript(ParameterMap pm,String index,String type);
	public int deltele(String id,String index,String type);
	public Map<String, Object> find(String id,String index,String type);
	public Object query(ParameterMap pm,String index,String type,Page page);
	public Object shouldQuery(ParameterMap pm,String index,String type,Page page);
	
}

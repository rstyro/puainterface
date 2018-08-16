package top.lrshuai.pua.dao;


import java.util.List;

import top.lrshuai.pua.plugin.Page;
import top.lrshuai.pua.util.ParameterMap;


public interface PublicDao {

	/**
	 * 保存点赞记录
	 * 
	 * @param pm
	 * @return
	 */
	public int savePraise(ParameterMap pm);
	
	/**
	 * 是否重复点赞
	 * 
	 * @param pm
	 * @return
	 */
	public ParameterMap repeatPraise(ParameterMap pm);
	
	/**
	 * 获取用户收藏列表
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getUserCollectList(ParameterMap pm);
	
	/**
	 * 收藏
	 * @param pm
	 * @return
	 */
	public int saveCollect(ParameterMap pm);
	
	/**
	 * 删除收藏
	 * @param pm
	 * @return
	 */
	public int delCollect(ParameterMap pm);
	
	/**
	 * 是否重复收藏
	 * @param pm
	 * @return
	 */
	public ParameterMap repeatCollect(ParameterMap pm);
	
	/**
	 * 发布一条系统消息
	 * @param pm
	 * @return
	 */
	public int saveSystemMsg(ParameterMap pm);
	
	/**
	 * 是否重复标记为已读
	 * @param pm
	 * @return
	 */
	public ParameterMap repeatReadSystem(ParameterMap pm);
	
	/**
	 * 保存系统消息的用户已读记录
	 * @param pm
	 * @return
	 */
	public int saveSysMsgRead(ParameterMap pm);
	
	/**
	 * 是否有未读的系统消息
	 * @param pm
	 * @return
	 */
	public ParameterMap hasSysMsg(ParameterMap pm);
	
	/**
	 * 获取用户已读的消息列表
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getUserSysMsgReadlistPage(Page page);
	
	/**
	 * 删除用户已读信息
	 * @param pm
	 * @return
	 */
	public int delSysMsgRead(ParameterMap pm);
	

	/**
	 * 保存用户反馈
	 * @param pm
	 * @return
	 */
	public int saveUserFeedback(ParameterMap pm);
	
	/**
	 * 保存用户投稿
	 * @param pm
	 * @return
	 */
	public int saveContribut(ParameterMap pm);
}

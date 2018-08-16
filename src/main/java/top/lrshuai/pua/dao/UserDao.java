package top.lrshuai.pua.dao;


import top.lrshuai.pua.util.ParameterMap;

public interface UserDao {

	/**
	 * 保存用户信息
	 * 
	 * @param pm
	 * @return
	 */
	public int saveUser(ParameterMap pm);
	
	
	/**
	 * 保存用户头像
	 * @param pm
	 */
	public void saveUserPictrue(ParameterMap pm);

	/**
	 * 更新用户信息
	 * 
	 * @param pm
	 * @return
	 */
	public int updateUserInfo(ParameterMap pm);
	
	/**
	 * 逻辑删除图片
	 * @param pm
	 * @return
	 */
	public int delPicture(ParameterMap pm);

	/**
	 * 获取用户信息
	 * 
	 * @param pm
	 * @return
	 */
	public ParameterMap getUserInfo(ParameterMap pm);
	
	
}

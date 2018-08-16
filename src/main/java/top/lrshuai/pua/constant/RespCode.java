package top.lrshuai.pua.constant;

/**
 * 接口返回 状态码
 * @author tyro
 *
 */
public class RespCode {
	//成功
	public static final int SUCCESS=200;
	
	//失败，一般是参数出问题或者一些其他原因不满足
	public static final int FAILED=400;
	
	//session 过期
	public static final int SESSION_EXPIRE=401;
	
	//token 过期
	public static final int TOKEN_EXPIRE=402;
	
	//未登录，或者没有权限访问
	public static final int NOT_AUTH=403;
	
	//没找到
	public static final int NOT_FOUND=404;
	
	//空值错误
	public static final int KEY_NULL=405;
	
	//一般是服务器错误
	public static final int ERROR=500;
}

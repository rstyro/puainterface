package top.lrshuai.pua.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import top.lrshuai.pua.constant.RespCode;
import top.lrshuai.pua.constant.SessionConst;
import top.lrshuai.pua.dao.PublicDao;
import top.lrshuai.pua.dao.UserDao;
import top.lrshuai.pua.service.UserService;
import top.lrshuai.pua.util.DateUtil;
import top.lrshuai.pua.util.HttpUtils;
import top.lrshuai.pua.util.ImgUtil;
import top.lrshuai.pua.util.MyUtil;
import top.lrshuai.pua.util.ParameterMap;
import top.lrshuai.pua.util.ResponseModel;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PublicDao publicDao;
	
	@Autowired
	private RedisTemplate<String, String> redis; 
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Value("${weixin.app.key}")
	private String weixinAppKey;
	
	@Value("${weixin.app.secret}")
	private String weixinAppSecret;
	
	//图片上次根目录
	@Value("${upload.root.folder}")
	public String root_fold;
	
	@Override
	public Map<String, Object> login(ParameterMap pm, HttpSession session) {
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> data = new HashMap<>();
		try {
			log.info("pm="+pm);
			log.info("sessionid="+session.getId());
			String code = pm.getString("code");
			if("".equals(code)){
				map.put("msg", "code 参数错误");
				map.put("status", RespCode.ERROR);
				return map;
			}
			String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+weixinAppKey+"&secret="+weixinAppSecret+"&js_code="+code+"&grant_type=authorization_code";
			String result =  HttpUtils.getInstance().sendGetMethod(url, "UTF-8");
			JSONObject obj = new JSONObject(result);
			String openId = obj.getString("openid");
			pm.put("openid", openId);
			String redisToken = redis.opsForValue().get(openId);
			if(redisToken != null && !"".equals(redisToken)){
				redis.delete(redisToken);
			}
			ParameterMap user = userDao.getUserInfo(pm);
			System.out.println("userInfo="+user);
			if(user == null || "".equals(user.getString("user_id"))){
				String picId = MyUtil.getUUID();
				pm.put("pic_id", picId);
				pm.put("create_time", DateUtil.getTime());
				userDao.saveUser(pm);
				savePictrue(pm.getString("user_url"),picId,pm.getString("user_id"));
				user = userDao.getUserInfo(pm);
			}
			
			//请求是否有用户消息，
			data.put("hasMsg", hasSysMsg(user));
			
			session.setAttribute(SessionConst.USER_SESSION, user);
			//返回一个新的token,为了用户session 过期之后的自动登陆
			String token = MyUtil.getUUID();
			
			/*
			 * redis 缓存15天
			 * 缓存是为了，如果用户在其他的手机登录微信，将把以前的token 删除掉
			 * */
			redis.opsForValue().set(token, openId, 15, TimeUnit.DAYS);
			redis.opsForValue().set(openId, token, 15, TimeUnit.DAYS);
			data.put("session_id", session.getId());
			data.put("token", token);
			data.put("userInfo", user);
			map.put("data", data);
			map.put("status", 200);
			map.put("msg", "ok");
		}catch(org.json.JSONException e){
			log.error(e.getMessage(), e);
			map.put("status", 403);
			map.put("msg", "code 过期");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			map.put("status", 500);
			map.put("msg", e.getMessage());
		}
		return map;
	}
	
	@Override
	public Map<String, Object> autoLogin(ParameterMap pm, HttpSession session) {
		Map<String,Object> data = new HashMap<>();
		try {
			String token = pm.getString("token");
			if("".equals(token)){
				return ResponseModel.getModel("token 为空",RespCode.KEY_NULL, null);
			}
			String openId = redis.opsForValue().get(token);
			if(openId == null || "".equals(openId)){
				return ResponseModel.getModel("token 过期", RespCode.TOKEN_EXPIRE, null);
			}
			ParameterMap mm = new ParameterMap();
			mm.put("openid", openId);
			ParameterMap user = userDao.getUserInfo(mm);
			
			data.put("hasMsg", hasSysMsg(user));
			
			session.setAttribute(SessionConst.USER_SESSION, user);
			data.put("session_id", session.getId());
			data.put("userInfo", user);
			return ResponseModel.getModel("ok", RespCode.SUCCESS,data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	
	@Override
	public Map<String, Object> updateUserInfo(ParameterMap pm, HttpSession session) {
		Map<String,Object> data = new HashMap<>();
		try {
			
			ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
			if(user == null || "".equals(user.getString("user_id"))){
				return ResponseModel.getModel("未登录", RespCode.NOT_AUTH, null);
			}
			pm.put("user_id", user.getString("user_id"));
			log.info("updateUserInfo,pm="+pm);
			userDao.updateUserInfo(pm);
			return ResponseModel.getModel("ok", RespCode.SUCCESS,data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	
	
	@Override
	public Object uploadImg(HttpSession session,MultipartFile file) {
		try {
			
			ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
			if(user == null || "".equals(user.getString("user_id"))){
				return ResponseModel.getModel("未登录", RespCode.NOT_AUTH, null);
			}
			
			ParameterMap parame = new ParameterMap();
			//更新用户信息
			String pic_id = MyUtil.getUUID();
			parame.put("user_id", user.getString("user_id"));
			parame.put("pic_id", pic_id);
			userDao.updateUserInfo(parame);
			
			//保存新的图片
			parame.put("pic_type","user");
			String picPath = "/user/"+MyUtil.random(8) + ".png";
			// 加/upload 是因为upload 是根目录
			parame.put("pic_path", "/upload"+picPath);
			InputStream in = file.getInputStream();
			ImgUtil.uploadImg(root_fold, picPath, in);
			userDao.saveUserPictrue(parame);
			//删除旧的图片
			userDao.delPicture(user);
			return parame.getString("pic_path");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return "";
		}
	}
	
	private boolean hasSysMsg(ParameterMap user) {
		ParameterMap hasMsg = publicDao.hasSysMsg(user);
		if(hasMsg != null) {
			String res = hasMsg.getString("result");
			int resNum = Integer.parseInt(res);
			if(resNum > 0) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 保存用户头像
	 * @param url
	 * @param picId
	 * @param picType
	 */
	private void savePictrue(String url,String picId,String userId){
		try {
			ParameterMap param = new ParameterMap();
			param.put("pic_id",picId);
			param.put("user_id",userId);
			param.put("pic_type","user");
			String picPath = "/user/"+MyUtil.random(8) + ".png";
			// 加/upload 是因为upload 是根目录
			param.put("pic_path", "/upload"+picPath);
			ImgUtil.saveImgByUrl(url, root_fold + picPath);
			userDao.saveUserPictrue(param);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}

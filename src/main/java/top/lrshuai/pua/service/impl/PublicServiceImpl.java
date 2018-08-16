package top.lrshuai.pua.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.lrshuai.pua.constant.RespCode;
import top.lrshuai.pua.constant.SessionConst;
import top.lrshuai.pua.dao.PublicDao;
import top.lrshuai.pua.es.dao.Dao;
import top.lrshuai.pua.plugin.Page;
import top.lrshuai.pua.service.CacheService;
import top.lrshuai.pua.service.PublicService;
import top.lrshuai.pua.task.EmailThread;
import top.lrshuai.pua.util.DateUtil;
import top.lrshuai.pua.util.MyUtil;
import top.lrshuai.pua.util.ParameterMap;
import top.lrshuai.pua.util.ResponseModel;

@Service
@Transactional
public class PublicServiceImpl implements PublicService{

	@Autowired
	private PublicDao publicDao;
	
	@Autowired
	private Dao dao;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private CacheService cacheService;
	
	
	@Override
	public Map<String, Object> savePraise(ParameterMap pm, HttpSession session) {
		try {
			String tableType = pm.getString("table_type");
			String tableId  = pm.getString("table_id");
			String _id = pm.getString("_id");
			if("".equals(tableType) || "".equals(tableId) || "".equals(_id)){
				return ResponseModel.getModel("参数不全", RespCode.KEY_NULL, null);
			}
			ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
			if(user != null && !"".equals(user.getString("user_id"))){
				pm.put("user_id", user.getString("user_id"));
				log.info("repeatPraise pm="+pm);
				ParameterMap repeat = publicDao.repeatPraise(pm);
				log.info("repeatPraise="+repeat);
				if(repeat == null || "".equals(repeat.getString("praise_id"))){
					pm.put("user_id",user.getString("user_id"));
					publicDao.savePraise(pm);
					updatepraiseNum(_id);
				}else{
					log.info("已经赞过了");
				}
			}else{
				String key = SessionConst.IS_PRAISE+tableType+tableId;
				Boolean isPraise = (Boolean) session.getAttribute(key);
				if(isPraise == null || isPraise == false){
					System.out.println("if isPraise="+isPraise);
					updatepraiseNum(_id);
					session.setAttribute(key, true);
				}
				
			}
			return ResponseModel.getModel("ok", RespCode.SUCCESS, null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	
	/**
	 * 更新点赞数
	 * @param _id 	文档_id
	 */
	public void updatepraiseNum(String _id){
		ParameterMap mm = new ParameterMap();
		mm.put("_id", _id);
		mm.put("script_text", "ctx._source.praise_num += 1");
		dao.updateByScript(mm, "pua", "speechcraft");
	}

	@Override
	public Map<String, Object> saveCollect(ParameterMap pm, HttpSession session) {
		Map<String,Object> data = new HashMap<>();
		try {
			String speechcraftId = pm.getString("speechcraft_id");
			ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
			if(user == null || "".equals(user.getString("user_id"))){
				return ResponseModel.getModel("未登录", RespCode.NOT_AUTH, null);
			}else if("".equals(speechcraftId)){
				return ResponseModel.getModel("参数不全", RespCode.KEY_NULL, null);
			}
			String userId = user.getString("user_id");
			pm.put("user_id", userId);
			ParameterMap repeat = publicDao.repeatCollect(pm);
			if(repeat == null || "".equals(repeat.getString("collect_id"))){
				publicDao.saveCollect(pm);
				data.put("collect_flag", true);
			}else{
				publicDao.delCollect(pm);
				data.put("collect_flag", false);
			}
			return ResponseModel.getModel("ok", RespCode.SUCCESS,data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	/**
	 * 保存反馈
	 */
	@Override
	public Map<String, Object> saveFeedback(ParameterMap pm, HttpSession session) {
		Map<String,Object> data = new HashMap<>();
		try {
			String content = pm.getString("content");
			ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
			if(user == null || "".equals(user.getString("user_id"))){
				return ResponseModel.getModel("未登录", RespCode.NOT_AUTH, null);
			}else if("".equals(content)){
				return ResponseModel.getModel("参数不全", RespCode.KEY_NULL, null);
			}
			EmailThread et = new EmailThread(mailService,"1006059906@qq.com", "用户反馈",content);
			new Thread(et).start();
			String userId = user.getString("user_id");
			pm.put("user_id", userId);
			publicDao.saveUserFeedback(pm);
			return ResponseModel.getModel("ok", RespCode.SUCCESS,data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	
	@Override
	public Map<String, Object> saveContribute(ParameterMap pm, HttpSession session) {
		Map<String,Object> data = new HashMap<>();
		try {
			String content = pm.getString("content");
			ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
			if(user == null || "".equals(user.getString("user_id"))){
				return ResponseModel.getModel("未登录", RespCode.NOT_AUTH, null);
			}else if("".equals(content)){
				return ResponseModel.getModel("参数不全", RespCode.KEY_NULL, null);
			}
			String userId = user.getString("user_id");
			pm.put("user_id", userId);
			pm.put("create_time", DateUtil.getTime());
			publicDao.saveContribut(pm);
			
			return ResponseModel.getModel("ok", RespCode.SUCCESS,data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	
	/**
	 * 发布系统消息
	 */
	@Override
	public Map<String, Object> saveSystemMsg(ParameterMap pm, HttpSession session) {
		Map<String,Object> data = new HashMap<>();
		try {
			String content = pm.getString("msg_content");
			String createTime = pm.getString("create_time");
			if(createTime == null || "".equals(createTime)) {
				pm.put("create_time", DateUtil.getTime());
			}
			ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
			if(user == null || "".equals(user.getString("user_id"))){
				return ResponseModel.getModel("未登录", RespCode.NOT_AUTH, null);
			}else if("".equals(content)){
				return ResponseModel.getModel("参数不全", RespCode.KEY_NULL, null);
			}else if(!"1".equals(user.getString("user_id"))) {
				return ResponseModel.getModel("权限不够", RespCode.NOT_AUTH, null);
			}
			publicDao.saveSystemMsg(pm);
			return ResponseModel.getModel("ok", RespCode.SUCCESS,data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	
	@Override
	public Map<String, Object> getSysMsgList(ParameterMap pm, HttpSession session) {
		Page page = new Page();
		String pageNo =pm.getString("page_no");
		String pageSize =pm.getString("page_size");
		try {
			if(MyUtil.notEmpty(pageNo)){
				page.setCurrentPage(Integer.parseInt(pageNo));
			}
			if(MyUtil.notEmpty(pageSize)){
				page.setShowCount(Integer.parseInt(pageSize));
			}else{
				page.setShowCount(10);
			}
			ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
			log.info("user="+user);
			if(user == null || "".equals(user.getString("user_id"))){
				return ResponseModel.getModel("没有登录状态", RespCode.NOT_AUTH, null);
			}
			page.setPm(user);
			List<ParameterMap> sysMsgList = publicDao.getUserSysMsgReadlistPage(page);
			
			if(sysMsgList == null){
				sysMsgList = new ArrayList<>();
			}
			return ResponseModel.getModel("ok", RespCode.SUCCESS, sysMsgList, page);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	@Override
	public Map<String, Object> tabSysMsgRead(ParameterMap pm, HttpSession session) {
		Map<String,Object> data = new HashMap<>();
		try {
			String msgId = pm.getString("msg_id");
			ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
			if(user == null || "".equals(user.getString("user_id"))){
				return ResponseModel.getModel("未登录", RespCode.NOT_AUTH, null);
			}else if("".equals(msgId)){
				return ResponseModel.getModel("参数不全", RespCode.KEY_NULL, null);
			}
			pm.put("user_id", user.getString("user_id"));
			ParameterMap repeat = publicDao.repeatReadSystem(pm);
			if(repeat == null || "".equals(repeat.getString("read_id"))) {
				publicDao.saveSysMsgRead(pm);
			}
			return ResponseModel.getModel("ok", RespCode.SUCCESS,data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	@Override
	public Map<String, Object> delTabSysMsgRead(ParameterMap pm, HttpSession session) {
		Map<String,Object> data = new HashMap<>();
		try {
			String readId = pm.getString("read_id");
			ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
			if(user == null || "".equals(user.getString("user_id"))){
				return ResponseModel.getModel("未登录", RespCode.NOT_AUTH, null);
			}else if("".equals(readId)){
				return ResponseModel.getModel("参数不全", RespCode.KEY_NULL, null);
			}
			pm.put("user_id", user.getString("user_id"));
			ParameterMap repeat = publicDao.repeatReadSystem(pm);
			if(repeat == null || "".equals(repeat.getString("read_id"))) {
				pm.put("is_del", "1");
				publicDao.saveSysMsgRead(pm);
			}else {
				publicDao.delSysMsgRead(pm);
			}
			return ResponseModel.getModel("ok", RespCode.SUCCESS,data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	
	@Override
	public Map<String, Object> getAboutList() {
		return cacheService.getAbout();
	}
	
}

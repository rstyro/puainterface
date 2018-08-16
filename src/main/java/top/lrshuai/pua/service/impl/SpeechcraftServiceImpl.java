package top.lrshuai.pua.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.lrshuai.pua.constant.PraiseType;
import top.lrshuai.pua.constant.RespCode;
import top.lrshuai.pua.constant.SessionConst;
import top.lrshuai.pua.dao.PublicDao;
import top.lrshuai.pua.es.dao.Dao;
import top.lrshuai.pua.plugin.Page;
import top.lrshuai.pua.service.SpeechcraftService;
import top.lrshuai.pua.util.MyUtil;
import top.lrshuai.pua.util.ParameterMap;
import top.lrshuai.pua.util.ResponseModel;

@Service
@Transactional
public class SpeechcraftServiceImpl implements SpeechcraftService{

	private Logger log = Logger.getLogger(getClass());
	
	//索引名称（数据库名）
	private String index = "pua";
	
	//类型名称（表名）
	private String type = "speechcraft"; 
	
	
	@Autowired
	private Dao dao;
	
	@Autowired
	private PublicDao publicDao;
	
	@Autowired
	private RedisTemplate<String, String> redis;
	
	@Override
	public Map<String, Object> save(ParameterMap pm, HttpSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> find(ParameterMap pm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> del(ParameterMap pm, HttpSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> update(ParameterMap pm, HttpSession session) {
		try {
			int issuccess = dao.update(pm, index, type);
			return ResponseModel.getModel("ok", "success", issuccess);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return ResponseModel.getErrorModel("更新错误");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getSpeechraftList(ParameterMap pm, HttpSession session) {
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
			String keyword = pm.getString("kw");
			String speechraftType = pm.getString("type");
			keyword = QueryParser.escape(keyword);
			ParameterMap mm = new ParameterMap();
			if(MyUtil.notEmpty(keyword)){
				mm.put("must_key", "content");
				mm.put("content_value", keyword);
			}else{
				//默认列表按时间排序
				mm.put("sort", "time");
			}
			if(MyUtil.notEmpty(speechraftType)){
				mm.put("must_key2", "type");
				mm.put("type_value", speechraftType);
			}
			List<Map<String,Object>> list = (List<Map<String, Object>>) dao.query(mm, index, type, page);
			if(list == null){
				list = new ArrayList<>();
			}
			//当在elasticsearch 查询的时候，page 的页数减一，所以这里重置回来
			if(MyUtil.notEmpty(pageNo)){
				page.setCurrentPage(Integer.parseInt(pageNo));
			}
			for(Map<String,Object> speechraft:list){
				String speechcraftId = (String) speechraft.get("speechcraft_id");
				try {
					String authId = (String) speechraft.get("user_id");
					String nickName = redis.opsForValue().get(authId);
					if(nickName != null && !"".equals(nickName)){
						speechraft.put("auther", nickName);
					}else{
						speechraft.put("auther", "帅大叔");
					}
					
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					speechraft.put("auther", "帅大叔");
				}
				
				//点赞状态 
				ParameterMap user = (ParameterMap) session.getAttribute(SessionConst.USER_SESSION);
				log.info("user="+user);
				if(user != null && !"".equals(user.getString("user_id"))){
					//点赞
					ParameterMap parametm = new ParameterMap();
					parametm.put("table_id", speechcraftId);
					parametm.put("user_id", user.getString("user_id"));
					parametm.put("table_type", PraiseType.SPEECHCRAFT);
					log.info("parametm="+parametm);
					ParameterMap repeatPraise = publicDao.repeatPraise(parametm);
					if(repeatPraise == null || "".equals(repeatPraise.getString("praise_id"))){
						speechraft.put("praise_flag", false);
					}else{
						speechraft.put("praise_flag", true);
					}
					
					//收藏
					parametm.put("speechcraft_id", speechcraftId);
					ParameterMap repeatCollect = publicDao.repeatCollect(parametm);
					if(repeatCollect == null || "".equals(repeatCollect.getString("collect_id"))){
						speechraft.put("collect_flag", false);
					}else{
						speechraft.put("collect_flag", true);
					}
					
				}else{
					speechraft.put("praise_flag", 0);
					String key = SessionConst.IS_PRAISE+"speechcraft"+speechcraftId;
					Boolean isPraise = (Boolean) session.getAttribute(key);
					if(isPraise != null && isPraise){
						speechraft.put("praise_flag", 1);
					}
					speechraft.put("collect_flag", 0);
				}
			}
			
			return ResponseModel.getModel("ok", RespCode.SUCCESS, list, page);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getUserCollectList(ParameterMap pm, HttpSession session) {
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
			List<ParameterMap> collectIds = publicDao.getUserCollectList(user);
			log.info("collectIds="+collectIds);
			List<Map<String,Object>> list = null;
			if(collectIds != null && collectIds.size() > 0){
				ParameterMap parame = new ParameterMap();
				parame.put("should_key", "speechcraft_id");
				String values = "";
				for(ParameterMap collect:collectIds){
					values = values + collect.getString("speechcraft_id")+",";
				}
				log.info("values1="+values);
				values = values.substring(0, values.length()-1);
				log.info("values2="+values);
				parame.put("should_value", values);
				log.info("parame="+parame);
				
				//这是是为了按时间排序
				String sort = pm.getString("sort");
				if(sort != null && !"".equals(sort)) {
					parame.put("sort", sort);
				}
				
				list = (List<Map<String, Object>>) dao.shouldQuery(parame, index, type, page);
				log.info("list="+list);
				
				//当在elasticsearch 查询的时候，page 的页数减一，所以这里重置回来
				if(MyUtil.notEmpty(pageNo)){
					page.setCurrentPage(Integer.parseInt(pageNo));
				}
			}
			if(list == null){
				list = new ArrayList<>();
			}
			return ResponseModel.getModel("ok", RespCode.SUCCESS, list, page);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return ResponseModel.getModel(e.getMessage(), RespCode.ERROR, null);
		}
	}

}

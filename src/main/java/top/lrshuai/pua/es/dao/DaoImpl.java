package top.lrshuai.pua.es.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.lrshuai.pua.plugin.Page;
import top.lrshuai.pua.util.ParameterMap;


@Service
public class DaoImpl implements Dao {

	@Autowired
	private TransportClient transportClient;

	private Logger log = Logger.getLogger(getClass());

	@Override
	public String save(ParameterMap pm, String index, String type) {
		try {
			XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
			for (Object key : pm.keySet()) {
				System.out.println("key= " + key + " and value= " + pm.get(key));
				if (key != null && !"".equals((String) key)) {
					builder.field((String) key, pm.get(key));
				}
			}
			builder.endObject();
			IndexResponse response = this.transportClient.prepareIndex(index, type).setSource(builder).get();
			return response.getId();
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public int update(ParameterMap pm, String index, String type) {
		System.out.println("upate - pm="+pm);
		UpdateRequest request = new UpdateRequest(index, type, pm.getString("_id"));
		try { 
			XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
			for (Object key : pm.keySet()) {
				System.out.println("key= " + key + " and value= " + pm.get(key));
				if (key != null && !"".equals((String) key) && !"_id".equals(key)) {
					builder.field((String) key, pm.get(key));
				}
			}
			builder.endObject();
			request.doc(builder);
			UpdateResponse response = transportClient.update(request).get();
			System.out.println("response="+response);
			return response.getShardInfo().getSuccessful();
		} catch (IOException | InterruptedException | ExecutionException e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}
	
	@Override
	public int updateByScript(ParameterMap pm, String index, String type) {
		UpdateRequest request = new UpdateRequest(index, type, pm.getString("_id"));
		try { 
			String scriptText = pm.getString("script_text");
			request.script(new Script(scriptText));
			UpdateResponse response = transportClient.update(request).get();
			return response.getShardInfo().getSuccessful();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public int deltele(String id, String index, String type) {
		DeleteResponse response = transportClient.prepareDelete(index, type, id).get();
		System.out.println("response="+response);
		return response.getShardInfo().getSuccessful();
	}

	@Override
	public Map<String, Object> find(String id, String index, String type) {
		GetResponse response = transportClient.prepareGet(index, type, id).get();
		Map<String, Object> result = response.getSource();
		if (result != null) {
			result.put("_id", response.getId());
		}
		return result;
	}

	@Override
	public Object query(ParameterMap pm, String index, String type,Page page) {
		List<Map<String, Object>> result = new ArrayList<>();
		String termKeyReg = "term_key.*";
		String mustKeyReg = "must_key.*";
		String shouldKeyReg = "should_key.*";
		try {
			BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
			for (Object keyO : pm.keySet()) {
				String key = (String) keyO;
				if (key != null && !"".equals(key)) {
					if(key.matches(termKeyReg)){
						//term 查询
						String termKey = pm.getString(key);
						boolBuilder.must(QueryBuilders.termQuery(termKey, pm.getString(termKey+"_value")).boost(3));
					}else if (key.matches(mustKeyReg)) {
						//must 查询
						String mustKey = pm.getString(key);
						boolBuilder.must(QueryBuilders.matchQuery(mustKey, pm.getString(mustKey+"_value")).boost(2));
					} else if (key.matches(shouldKeyReg)) {
						//should 查询
						String shouldKey = pm.getString(key);
						boolBuilder.should(QueryBuilders.matchQuery(shouldKey, pm.getString(shouldKey+"_value")));
					}
				}
			}
			
			SearchRequestBuilder builder = transportClient.prepareSearch(index).setTypes(type)
					.setSearchType(SearchType.QUERY_THEN_FETCH).setQuery(boolBuilder);
			log.info(String.valueOf(builder));
			if(page != null){
				builder.setFrom(page.getCurrentPage() <= 1?0:((page.getCurrentPage()-1)*page.getShowCount()));
				builder.setSize(page.getShowCount());
			}else{
				builder.setFrom(0);
				builder.setSize(10);
			}
//			HighlightBuilder hBuilder = new HighlightBuilder();
//            hBuilder.preTags("<h2>");
//            hBuilder.postTags("</h2>");    
//            hBuilder.field("question"); 
//            builder.highlighter(hBuilder);
			String sort = pm.getString("sort");
			if("time".equals(sort)){
				builder.addSort("create_time", SortOrder.DESC);
			}
			SearchResponse response = builder.get();
			long totle = response.getHits().getTotalHits();
			if(page != null){
				page.setTotalResult(totle);
			}
			SearchHits hits = response.getHits();
			for(SearchHit hit:hits){
				Map<String,Object> map = hit.getSourceAsMap();
				if(map != null){
					map.put("_id", hit.getId());
					map.put("score", Float.isNaN(hit.getScore())?1:hit.getScore());
				}
				result.add(map);
			}
			//response.getHits().forEach((s) ->result.add(s.getSource()));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return result;
	}
	
	@Override
	public Object shouldQuery(ParameterMap pm, String index, String type, Page page) {
		List<Map<String, Object>> result = new ArrayList<>();
		try {
			BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
			//should 查询
			String realKey = pm.getString("should_key");
			String[] values = pm.getString("should_value").split(",");
			if(values != null && values.length > 0){	
				for(int i=0;i<values.length;i++){					
					boolBuilder.should(QueryBuilders.matchQuery(realKey, values[i]));
				}
			}
			SearchRequestBuilder builder = transportClient.prepareSearch(index).setTypes(type)
					.setSearchType(SearchType.QUERY_THEN_FETCH).setQuery(boolBuilder);
			log.info(String.valueOf(builder));
			log.info(page);
			if(page != null){
				builder.setFrom(page.getCurrentPage() <= 1?0:((page.getCurrentPage()-1)*page.getShowCount()));
				builder.setSize(page.getShowCount());
			}else{
				builder.setFrom(0);
				builder.setSize(10);
			}
			String sort = pm.getString("sort");
			if("time".equals(sort)){
				log.info("create_time sort");
				builder.addSort("create_time", SortOrder.DESC);
			}
			SearchResponse response = builder.get();
			long totle = response.getHits().getTotalHits();
			if(page != null){
				page.setTotalResult(totle);
			}
			SearchHits hits = response.getHits();
			for(SearchHit hit:hits){
				Map<String,Object> map = hit.getSourceAsMap();
				if(map != null){
					map.put("_id", hit.getId());
					map.put("score", Float.isNaN(hit.getScore())?1:hit.getScore());
				}
				result.add(map);
			}
			//response.getHits().forEach((s) ->result.add(s.getSource()));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return result;
	}
	
	
}

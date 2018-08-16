package top.lrshuai.pua.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.util.JSONPObject;
//import com.hankcs.hanlp.seg.common.Term;
//import com.hankcs.hanlp.tokenizer.BasicTokenizer;

import top.lrshuai.pua.plugin.Page;


public class MyUtil {

	private static String time = "yyyy-MM-dd hh:mm:ss";

	/**
	 * 获取32位UUID
	 * 
	 * @return
	 */
	public static String getUUID() {
		String str = UUID.randomUUID().toString();
		return str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23)
				+ str.substring(24);
	}

	/**
	 * 获取当前时间 格式:yyyy-MM-dd hh:mm:ss
	 * 
	 * @return
	 */
	public static String getTime() {
		return new SimpleDateFormat(time).format(new Date());
	}

	/**
	 * json回调,jsonp跨域
	 * 
	 * @param pd
	 * @param map
	 * @return
	 */
	public static Object returnObject(ParameterMap pm, Map<?, ?> map) {
		if (pm.containsKey("callback")) {
			String callback = pm.get("callback").toString();
			return new JSONPObject(callback, map);
		} else {
			return map;
		}
	}

	/**
	 * 验证手机号格式是否合格
	 * 
	 * @param phone
	 *            手机号
	 * @return boolean
	 */
	public static Boolean testPhone(String phone) {
		if (null == phone) {
			return false;
		}
		String reg = "^(1(3|4|5|7|8)\\d{9})$";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(phone);
		return m.matches();
	}

	/**
	 * list分页截取
	 * 
	 * @param list
	 *            目标集合
	 * @param pageNo
	 *            第几页
	 * @param pageSize
	 *            每页显示的大小
	 * @return
	 */
	public static List<ParameterMap> subList(List<ParameterMap> list, int pageNo, int pageSize) {
		if (list == null || list.size() == 0) {
			return list;
		}
		int totalSize = list.size();

		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > totalSize) {
			pageSize = totalSize;
		}
		int fromIndex = 0;
		int toIndex = 0;
		fromIndex = pageSize * (pageNo - 1);
		toIndex = pageSize * pageNo > totalSize ? totalSize : pageSize * pageNo;

		if (fromIndex == toIndex) {
			return new ArrayList<ParameterMap>();
		}
		return list.subList(fromIndex, toIndex);
	}

	/**
	 * 通过page 属性返回结果集
	 * 
	 * @param list
	 * @param page
	 * @return
	 */
	public static List<ParameterMap> getRusultList(List<ParameterMap> list, Page page) {
		if (list == null || list.size() == 0) {
			page.setTotalResult(0);
			return new ArrayList<>();
		}
		if (page == null) {
			throw new NullPointerException("this page is null");
		}
		int showCount = page.getShowCount();
		int currentPage = page.getCurrentPage();
		page.setTotalResult(list.size());

		int totalSize = list.size();

		if (currentPage <= 0) {
			currentPage = 1;
		}
		if (showCount > totalSize) {
			showCount = totalSize;
		}
		int fromIndex = 0;
		int toIndex = 0;
		fromIndex = showCount * (currentPage - 1);
		toIndex = showCount * currentPage > totalSize ? totalSize : showCount * currentPage;

		if (fromIndex == toIndex) {
			return new ArrayList<>();
		}
		return list.subList(fromIndex, toIndex);
	}
	
	/**
	 * 随机获取数据
	 * @param tagList 目标数组
	 * @param num 获取的个数
	 * @return
	 */
	public static List<ParameterMap> getRandomList(List<ParameterMap> tagList,int num){
		if(tagList == null) return null;
		if(num >= tagList.size()) {
			return tagList;
		}
		Set<ParameterMap> resultSet = new HashSet<>();
		while(true) {
			int r = new Random().nextInt(tagList.size()-1);
			resultSet.add(tagList.get(r));
			if(resultSet.size() == num) {
				break;
			}
		}
		return new ArrayList<ParameterMap>(resultSet);
	}

	/**
	 * url参数变成map返回
	 * 
	 * @param url
	 * @return
	 */
	public static ParameterMap getMapByUrlString(String url) {
		if (url == null || "".equals(url))
			return new ParameterMap();
		ParameterMap result = new ParameterMap();
		String[] arr = url.split("&");
		for (int i = 0; i < arr.length; i++) {
			String[] obj = arr[i].split("=");
			result.put(obj[0], obj[1]);
		}
		return result;
	}

	/**
	 * 随机生成六位数验证码
	 * 
	 * @return
	 */
	public static int getRandomNum() {
		Random r = new Random();
		return r.nextInt(900000) + 100000;// (Math.random()*(999999-100000)+100000)
	}

	/**
	 * 返回随机数
	 * 
	 * @param n
	 *            个数
	 * @return
	 */
	public static String random(int n) {
		if (n < 1 || n > 10) {
			throw new IllegalArgumentException("cannot random " + n + " bit number");
		}
		Random ran = new Random();
		if (n == 1) {
			return String.valueOf(ran.nextInt(10));
		}
		int bitField = 0;
		char[] chs = new char[n];
		for (int i = 0; i < n; i++) {
			while (true) {
				int k = ran.nextInt(10);
				if ((bitField & (1 << k)) == 0) {
					bitField |= 1 << k;
					chs[i] = (char) (k + '0');
					break;
				}
			}
		}
		return new String(chs);
	}

	/**
	 * 指定范围的随机数
	 * 
	 * @param min
	 *            最小值
	 * @param max
	 *            最大值
	 * @return
	 */
	public static int getRandomNum(int min, int max) {
		Random random = new Random();
		int s = random.nextInt(max) % (max - min + 1) + min;
		return s;
	}

	/**
	 * 检测字符串是否不为空(null,"","null")
	 * 
	 * @param s
	 * @return 不为空则返回true，否则返回false
	 */
	public static boolean notEmpty(String s) {
		return s != null && !"".equals(s) && !"null".equals(s);
	}

	/**
	 * 检测字符串是否为空(null,"","null")
	 * 
	 * @param s
	 * @return 为空则返回true，不否则返回false
	 */
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s) || "null".equals(s);
	}

	/**
	 * 字符串转换为字符串数组
	 * 
	 * @param str
	 *            字符串
	 * @param splitRegex
	 *            分隔符
	 * @return
	 */
	public static String[] str2StrArray(String str, String splitRegex) {
		if (isEmpty(str)) {
			return null;
		}
		return str.split(splitRegex);
	}

	/**
	 * 用默认的分隔符(,)将字符串转换为字符串数组
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static String[] str2StrArray(String str) {
		return str2StrArray(str, ",\\s*");
	}

	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，日期转字符串
	 * 
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String date2Str(Date date) {
		return date2Str(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date str2Date(String date) {
		if (notEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new Date();
		} else {
			return null;
		}
	}

	/**
	 * 按照参数format的格式，日期转字符串
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String date2Str(Date date, String format) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		} else {
			return "";
		}
	}

	/**
	 * 把时间根据时、分、秒转换为时间段
	 * 
	 * @param StrDate
	 */
	public static String getTimes(String StrDate) {
		String resultTimes = "";

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date now;

		try {
			now = new Date();
			java.util.Date date = df.parse(StrDate);
			long times = now.getTime() - date.getTime();
			long day = times / (24 * 60 * 60 * 1000);
			long hour = (times / (60 * 60 * 1000) - day * 24);
			long min = ((times / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long sec = (times / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

			StringBuffer sb = new StringBuffer();
			// sb.append("发表于：");
			if (hour > 0) {
				sb.append(hour + "小时前");
			} else if (min > 0) {
				sb.append(min + "分钟前");
			} else {
				sb.append(sec + "秒前");
			}

			resultTimes = sb.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return resultTimes;
	}

	/**
	 * 写txt里的单行内容
	 * 
	 * @param filePath
	 *            文件路径
	 * @param content
	 *            写入的内容
	 */
	public static void writeFile(String fileP, String content) {
		String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")) + "../../"; // 项目路径
		filePath = (filePath.trim() + fileP.trim()).substring(6).trim();
		if (filePath.indexOf(":") != 1) {
			filePath = File.separator + filePath;
		}
		try {
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(filePath), "utf-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(content);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber) {
		boolean flag = false;
		try {
			Pattern regex = Pattern
					.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 读取txt里的单行内容
	 * 
	 * @param filePath
	 *            文件路径
	 */
	@SuppressWarnings("resource")
	public static String readTxtFile(String fileP) {
		try {

			// String filePath =
			// String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))+"../../";
			// //项目路径
			String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")); // 项目路径
			filePath = filePath.replaceAll("file:/", "");
			filePath = filePath.replaceAll("%20", " ");
			filePath = filePath.trim() + fileP.trim();
			if (filePath.indexOf(":") != 1) {
				filePath = File.separator + filePath;
			}
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); // 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					return lineTxt;
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件,查看此路径是否正确:" + filePath);
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
		}
		return "";
	}

	/**
	 * list to String
	 * 
	 * @param list
	 * @param separator
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String listToString(List list, char separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i)).append(separator);
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}

	/**
	 * 获取总页数
	 * 
	 * @param pageSize
	 * @param totalSize
	 * @return
	 */
	public static int getTotalPage(int pageSize, int totalSize) {
		int pageNum = totalSize / pageSize;
		return totalSize > (pageNum * pageSize) ? pageNum + 1 : pageNum;
	}
	
	/**
	 * 给标签选中状态
	 * @param srcList
	 * @param tagList
	 */
	public static void checkLabelStatue(List<ParameterMap> srcList,List<ParameterMap> tagList){
		for(ParameterMap pm:srcList){
			pm.put("ischecked", "false");
			for(ParameterMap tag:tagList){
				if(pm.getString("label_id").equals(tag.getString("label_id"))){
					pm.put("ischecked", "true");
					break;
				}else{
					continue;
				}
			}
		}
	}
	
	/**
	 * 获取分词集合
	 * @param text
	 * @return
	 */
//	public static Set<String> setMentList(String text){
//		List<Term> keywords  = BasicTokenizer.segment(text);
//		System.out.println("keywords="+keywords);
//		Set<String> result = new HashSet<>();
//		for(Term term:keywords){
//			if(term.word != null && !"".equals(term.word.trim())) {
//				result.add(term.word);
//			}
//		}
//		System.out.println("result="+result);
//		return result;
//	}
	
	

	public static void main(String[] args) {
		//System.out.println(getRandomNum(0,0));
		String key = "must_keyasdfasfd";
		String reg = "must_key.*";
		System.out.println(key.matches(reg));
	}
	
}

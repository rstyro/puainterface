package top.lrshuai.pua.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

@Component
public class ImgUtil {
	
	 /**
	 * 上传文件
	 * @param filePath 文件名 
	 * @param in   io流
	 * @return  返回最终的路径
	 * @throws IOException 
	 */
	public static String uploadImg(String rootPATH,String filePath,InputStream in) throws IOException{
		System.out.println("rootPATH="+rootPATH);
		System.out.println("filePath="+filePath);
		String rusultPath = rootPATH+filePath;
		createFile(rusultPath);
		File realFile =new File(rusultPath);
		FileUtils.copyInputStreamToFile(in, realFile);
		return "/upload"+filePath;
	}
	
	/**
	 * 保存图片通过url
	 * 
	 * @param urlString
	 * @param filename
	 * @throws Exception
	 */
	public static void saveImgByUrl(String urlString, String filename) throws Exception {
		System.out.println("filename="+filename);
		System.out.println("urlString="+urlString);
		createFile(filename);
		// 构造URL
		URL url = new URL(urlString);
		// 打开连接
		URLConnection con = url.openConnection();
		// 输入流
		InputStream is = con.getInputStream();
		// 1K的数据缓冲
		byte[] bs = new byte[1024];
		// 读取到的数据长度
		int len;
		// 输出的文件流
		OutputStream os = new FileOutputStream(filename);
		// 开始读取
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		// 完毕，关闭所有链接
		os.close();
		is.close();
	}
	
	
	 /**
     * 文件或文件夹不存在则创建
     * @param dir 文件夹
     * @param filepath 文件名
     */
	public static void createFile(String dir,String filepath){
		File file = new File(dir);
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(dir+filepath);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("文件创建失败");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 创建文件，如果文件夹不存在将被创建
	 * 
	 * @param destFileName
	 *            文件路径
	 */
	public static File createFile(String destFileName) {
		File file = new File(destFileName);
		if (file.exists())
			return null;
		if (destFileName.endsWith(File.separator))
			return null;
		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs())
				return null;
		}
		try {
			if (file.createNewFile())
				return file;
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

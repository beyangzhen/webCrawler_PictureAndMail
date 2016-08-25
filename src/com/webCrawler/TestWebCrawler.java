package com.webCrawler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestWebCrawler {
	public static void main(String[] args) throws IOException {
		getPicture();    //爬取网站图片
		//getMailAddr(); //爬取网站邮箱
	}
	
	//IO流将网页上图片写入指定文件夹(采用多线程)
	class downloadPic implements Runnable{  
	    private String picUrl;  
	    private String fileDir;  
	    
	    downloadPic(String picUrl, String fileDir){  
	        this.picUrl = picUrl;  
	        this.fileDir = fileDir;  
	    }  
	    
	    @Override
	    public void run(){  
	        try {  
	            //截取路径的一部分作为图片名字    
	            int index = picUrl.lastIndexOf("/");  
	            int index1 = picUrl.lastIndexOf("?");  
	            index1 = index1==-1?picUrl.length():index1;  
	            String fileName = fileDir + picUrl.substring(index,index1);  
	            //创建放图片的目录
	            File dir = new File(fileDir);  
	            if(!dir.exists()) {
	            	dir.mkdirs();  
	            }
	            
	            //网站图片具体的url
	            URL url = new URL(picUrl.replaceAll(" ", "%20"));  
	            URLConnection conn = url.openConnection();  
	            //从图片url链接中，读取图片具体内容
	            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
	            //将图片内容写入指定文件夹
	            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(fileName)));  
	              
	            int len = 0;  
	            byte[] b = new byte[1024];  
	            //依次读取整个图片
	            while(-1 != (len=bis.read(b))){  
	            	//依次将图片写入文件夹
	                bos.write(b, 0, len);  
	                bos.flush();  
	            }  
	            bis.close();  
	            bos.close();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
	}  
	
	//获取网页上的图片
	public static void getPicture() throws IOException {
		String fileDir = "picture";  
		String strUrl = "http://www.dytt8.net/html/gndy/dyzz/20160804/51623.html";
		URL url = new URL(strUrl);   
        URLConnection conn = url.openConnection();  
        BufferedReader bufr = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));  
          
        String line = null;  
        String PicReg = "<img .*? ?src=\"(http:.*?)\".*?( /)?>"; 
        Pattern p = Pattern.compile(PicReg);  
        int count = 0;  
        while((line=bufr.readLine()) != null){  
//            System.out.println(line);  
            Matcher m = p.matcher(line);  
            while(m.find()){ 
            	//读取的只是图片的标签链接
                String picUrl = m.group(1);  
                System.out.println(picUrl);
                new Thread(new TestWebCrawler().new downloadPic(picUrl, fileDir)).start();  
                count++;  
            }  
        }  
        System.out.println("获取图片数量:" + count);  
	}
	
	//获取网页上的邮箱
	public static void getMailAddr() throws IOException {
		File file = new File("mailAddress.txt");
		String strUrl = "http://www.qq.com/";
		URL url = new URL(strUrl);
		URLConnection conn = url.openConnection();
		BufferedReader bufln = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		BufferedWriter bufw = new BufferedWriter(new FileWriter(file));
		
		String str = null;
		//邮箱正则表达式
		String mailReg = "\\w+@\\w+(\\.\\w+)+"; 
		//String mailReg = "[a-zA-Z0-9_]{6,12}@[a-zA-Z0-9]+(\\.[a-zA-Z]+)+";
		Pattern p = Pattern.compile(mailReg);
		while((str=bufln.readLine()) != null) {
			Matcher m = p.matcher(str);
			while(m.find()) {
				String sMailAddr = m.group();
				bufw.write(sMailAddr, 0, sMailAddr.length());
				bufw.newLine();
				bufw.flush();
			}
		}
		bufw.close();
		bufln.close();
	}
	
}

package ad;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class myThreadDownFile implements Runnable{
	Element e;
	String folder,attr;
	myThreadDownFile(String folder,String attr, Element e){
		this.e=e;
		this.folder=folder;
		this.attr=attr;
	}
	public void run() {
		Main.loadFile(attr, folder, e);
	}
}
class myThreadDownloadHtml implements Runnable{
	String url;
	public myThreadDownloadHtml(String url) {
		this.url=url;
	}
	public void run() {
		Main.loadForTagA(url);
	}
}
public class Main {
	Main(){}
	static String server="https://www.suzuki.com.vn"; 
	static String server2="http://www.youtube.com";
	static final int NUMBER_OF_FILE=2000;// Số file muốn tải
	static final int NUMBER_OF_THREAD=5
			;
	static boolean checkAddToQueue=true;
	static LinkedBlockingQueue<String> listS=new  LinkedBlockingQueue<String>();
	static LinkedBlockingQueue<String> listS2=new  LinkedBlockingQueue<String>();
	static String rootPath="Z://a";
	//----------------------------------------------------------------------------------------------------------------------
	static String cleanUrl(String url)//Hàm để clean url trước khi ghép với server để get 
	{
		url=url.replace(server, "");
		url=url.replace(" ", "");
		url=url.replace(server2, "");
		url=url.replace(server.replace("https://", ""), "");
		while(url.indexOf("/")==0) {
			url=url.replaceFirst("/", "");
		}
		if(url.indexOf('.')==0) url=url.replaceFirst(".", "");
		return url;
	}
	static void changeHrefTagA(Element e)// Hàm này để đổi href của thẻ A thành đường dẫn file Local
	{
		String url=e.attr("href");
//		if(url.equals("http://portal.hcmus.edu.vn/")) {
//			System.out.println(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,");
//		}
		url=server+"/"+cleanUrl(url);
		System.out.println(url);
		try{
			if(new URL(url).openConnection()==null) 
				
				return;}
		catch(IOException i) {
			return;
		}	
		url=url.replace(server, "");
		url=Helper.cleanUrlForMakeDir(url);
		url=Main.rootPath+""+url+"/content.html";
		e.attr("href",url);
	}
    static void loadFile(String attr, String folder, Element e)// Hàm để load các File (image, css, js,...) {
    {	
    	System.out.println("Tải file"+e.attr(attr));
    	try {
    		//-------------------------------------------------------------------------
    		String url=e.attr(attr);
    		String url2=url;
			//------------------------tạo đường dẫn và check file đã tải chưa------------------------------------------
    		url=Helper.queryUrl(url);
    		url=folder+"/"+url;
			url=Helper.cleanUrlForMakeDir(url);
    		
    		
			File f=Helper.createFile(Main.rootPath+"/"+url);
			e.attr(attr,"file:///"+Main.rootPath+"/"+url);
			try{
				if(!f.createNewFile()) {
				System.out.println("File tải r ///////////////////////////////////////////////////////////////////////////");
				return;
			}}catch(IOException i) {
				System.out.println(Main.rootPath+"/"+url);
				//i.printStackTrace();
				return;
				
			}
			
			
			//-----------------------------------get File--------------------------------------------------------------
			InputStream input;
			url=url2;
    		try {
    			input= new URL(url).openStream();
    		}
    		catch(MalformedURLException m) {
    			System.out.println("Để ráng mở thêm lần nữa");
    			url=Main.server+url;
    			System.out.println(url);
    			input=new URL(url).openStream();
    			url=url.replace(Main.server, "");
    		}
			//-------------------------------------ghi file xuống-----------------------------------------------------
    		FileOutputStream fileOutputStream=new FileOutputStream(f);
		    byte dataBuffer[] = new byte[1024];
		    int bytesRead;
		    BufferedInputStream in = new BufferedInputStream(input);
		    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
		        fileOutputStream.write(dataBuffer, 0, bytesRead);
		    }
		    System.out.println("File tải thành công ------------------------------------------------------------------------------");
    	}catch(MalformedURLException m) {
			System.out.println("Không thể mở Stream");
		}catch (IOException a) {
			a.printStackTrace();
			
    		System.out.println("Lỗi tải file "+e.attr("href") );
		}
    }
    static void multiThreaDownloadFile(int numberOfThread, Queue<Element> queue, String folder, String attr) {
    	while(!queue.isEmpty()) {
	    	ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREAD);
			for(int i=0;i<NUMBER_OF_THREAD;i++){
				Element e=queue.poll();
				if(e!=null) {
	    			Runnable u= new myThreadDownFile(folder, attr, e);
	        		executor.execute(u);
	        	}
			}
			executor.shutdown();
			while(!executor.isTerminated()) {}
    	}
    }
    static void loadForTagA(String url) // Hàm để download HTML
    {
		if(url.indexOf('.')==0) url=url.replaceFirst(".", "");
		url=Main.server+"/"+Main.cleanUrl(url);
		Document doc=loadHtml.getDoc(url);
		System.out.println("Đang cố gắng get "+url);
		if(doc!=null) {
			System.out.println("url này chuẩn nè: "+ url);
			url=url.replace(Main.server, "");
			url=Helper.cleanUrlForMakeDir(url);
			Helper.createDir(Main.rootPath+"/"+url);
			File file=new File(Main.rootPath+"/"+url+"/content.html");
			for(Element i : doc.select("a[href]")) {
				if(checkAddToQueue&&!Main.listS2.contains(i.attr("href"))) {
					Main.listS.add(i.attr("href"));
					Main.listS2.add(i.attr("href"));
				}
				if(listS2.size()>NUMBER_OF_FILE) checkAddToQueue=false;
	    		changeHrefTagA(i);
	    	}
			Queue<Element> listFile=new LinkedList<Element>();
			int numberOfThread=5;
			for(Element i : doc.getElementsByTag("script")) {
	    		if(i.hasAttr("src")) {
	    			listFile.add(i);
	    		}
	    	}
			multiThreaDownloadFile(numberOfThread, listFile, "script", "src");
	    	for(Element i : doc.getElementsByTag("img")) {
	    		listFile.add(i);
	    	}
	    	multiThreaDownloadFile(numberOfThread, listFile, "image", "src");
	    	for(Element i : doc.getElementsByTag("link")) {
	    			listFile.add(i);
	    	}
	    	multiThreaDownloadFile(numberOfThread, listFile, "link", "href");
			loadHtml.writeDoc(Main.rootPath+"/"+url, doc);
		}
		else {
			System.out.println("không get được");
		}
	}
    static void loadIndex() {
    	Document doc=loadHtml.getDoc(server);
    	if(doc==null) {
    		System.out.println("Có thể là rớt mạng rồi đó");
    		return;
    	}
    	for(Element i : doc.getElementsByTag("script")) {
    		if(i.hasAttr("src")) {
    			loadFile("src", "script", i);
    		}
    	}
    	for(Element i : doc.select("a[href]")) {
    		listS.add(i.attr("href"));
    		listS2.add(i.attr("href"));
    		changeHrefTagA(i);
    	}
    	for(Element i : doc.getElementsByTag("img")) {
    		loadFile("src", "images", i);
    	}
    	for(Element i : doc.getElementsByTag("link")) {
    		if(!i.attr("type").equals("")) {
    			loadFile("href", "link", i);
    		}
    	}
    	loadHtml.writeIndexDoc(rootPath, doc);
    	while(!listS.isEmpty()) {
    		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREAD);
    		for(int i=0;i<NUMBER_OF_THREAD;i++){
    			String url=listS.poll();
    			if(url!=null) {
	    			Runnable u= new myThreadDownloadHtml(url);
	        		executor.execute(u);
	        	}
    			else {
    				System.out.println("het poll đc");
    			}
    		}
    		executor.shutdown();
    		while(!executor.isTerminated()) {}
    		System.out.println(listS.size());
    		System.out.println(listS2.size());
    	}
    	System.out.println("Đã tải xong website");
    }
    public static void main(String args[]) throws IOException {
    	loadIndex();
//    	Element a= new Element("a");
//    	a.attr("href","/");
//    	changeHrefTagA(a);
//    	System.out.println(a.attr("href"));
    	//http://portal.hcmus.edu.vn/
    	//System.out.println(Helper.queryUrl("/media/jui/js/jquery-noconflict.js?e4f2f027344c57cc0cd8d67225b49f2c"));
    }
}
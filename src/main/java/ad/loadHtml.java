
package ad;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class loadHtml {
	static Document getDoc(String url) {
		Document doc=null;
		try {
			Connection cn =Jsoup.connect(url);
			doc=cn.get();
		}
		catch (HttpStatusException e) {
		}catch (IOException e) {
		}
		catch  (java.lang.IllegalArgumentException e) {
		}
		return doc;
	}
	static boolean writeDoc(String path, Document doc) {
		String text = doc.html();
		Helper.createDir(path);
		FileOutputStream fileStream;
		try {
			File file=new File(path+"/content.html");
			fileStream = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
			writer.write(text);
			writer.flush();		   
			writer.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	static boolean writeIndexDoc(String path, Document doc) {//write index html page
		String text = doc.html();
		Helper.createDir(path);
		FileOutputStream fileStream;
		try {
			File file=new File(path+"/index.html");
			fileStream = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
			writer.write(text);
			writer.flush();		   
			writer.close();
			 return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

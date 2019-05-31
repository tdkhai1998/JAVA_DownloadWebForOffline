package downloadWeb;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Helper {
	static void createDir(String pathStr) {
        Path path = Paths.get(pathStr);
        try {
			Files.createDirectories(path);
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
	static File createFile(String url) {
		createDir(url.substring(0, url.lastIndexOf("/")+1));
		File f= new File(url);
		return f;
	}
	static String cleanUrlForMakeDir(String url) {
    	url=url.replace("\\", "");
		url=url.replace("?", "");
		url=url.replace(":", "");
		url=url.replace("*", "");
		url=url.replace("<", "");
		url=url.replace(">", "");
		url=url.replace("|", "");
		url=url.replace("//", "/");
		url=url.replace("#", "");
		return url;
    }
	static String queryUrl(String url) {
		if(url.lastIndexOf('?')==-1) return url;
		String url2=url.substring(0, url.lastIndexOf('?'));
		String url3=url.substring(url.lastIndexOf('?')+1);
		String url4=url2.substring(url2.lastIndexOf('.'));
		url2=url2.replace(url4, "");
		url=url2+"query"+url3+url4;
		return url;
	}
}

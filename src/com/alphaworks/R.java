package com.alphaworks;

import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Global resource handler. Assets are kept in the same package as this class (com.alphaworks)
 * so that they are accessible using the class loader.
 * @author nilesh
 *
 */
public class R {
	private R() {
		//pass
		
	}
	/**
	 * Singleton instance.
	 */
	private static R instance = null;
	
	static {
		instance = new R();
	}
	/**
	 * Create a new {@link ImageIcon ImageIcon} from the given path.
	 * @param path Path relative to the base assets folder.
	 * @return An object of {@link ImageIcon ImageIcon}, null if no such file exist.
	 */
	
	public static ImageIcon loadIcon(String path){
		try {
			return new ImageIcon(ImageIO.read(loadFile(path)));
		}
		catch(Exception e) {
			
		}
		
		return null;
	}
	
	/**
	 * Return an {@link InputStream InputStream} associated with the specified file.
	 * @param path Path relative to the base assets folder.
	 * @return {@link InputStream InputStream}. Null, if no such file was found!
	 */
	public static InputStream loadFile(String path) {
		InputStream is = null;
		URL fileURl = instance.getClass().getResource(path);
		if(fileURl != null)
				is = instance.getClass().getResourceAsStream(path);
			
		
		return is;
	}
}

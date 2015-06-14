/**
 * 
 */
package com.idrene.emefana.rest.resources;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class ResourceUtil {
	
	public static boolean isSummaryView(ResourceView view){
		return view == ResourceView.SUMMARY;
	}
	
	public enum STATUS{
		activate,
		deactivate
	}
	
	public static double[] nearLocationString(String nearLocationStr){
		//(-6.316667, 39.28333299999997)
		nearLocationStr.replace("(", "");
		nearLocationStr.replace(")", "");
		String[] locationStr = nearLocationStr.split(",");
		return new double[]{Double.valueOf(locationStr[0].trim()),Double.valueOf(locationStr[1].trim())};
	}

}

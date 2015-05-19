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

}

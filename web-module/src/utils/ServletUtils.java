package utils;

import logic.dataManagment.GraphManager;
import logic.dataManagment.MissionManager;
import logic.dataManagment.UserManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

//import static chat.constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String GRAPH_MANAGER_ATTRIBUTE_NAME = "graphManager";
	private static final String MISSION_MANAGER_ATTRIBUTE_NAME = "missionManager";


	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained un-synchronized for performance POV
	 */
	private static final Object userManagerLock = new Object();
	private static final Object graphManagerLock = new Object();
	private static final Object missionManagerLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {

		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static GraphManager getGraphManager(ServletContext servletContext) {

		synchronized (graphManagerLock) {
			if (servletContext.getAttribute(GRAPH_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(GRAPH_MANAGER_ATTRIBUTE_NAME, new GraphManager());
			}
		}
		return (GraphManager) servletContext.getAttribute(GRAPH_MANAGER_ATTRIBUTE_NAME);
	}

	public static MissionManager getMissionManager(ServletContext servletContext){
		synchronized (missionManagerLock) {
			if (servletContext.getAttribute(MISSION_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(MISSION_MANAGER_ATTRIBUTE_NAME, new MissionManager());
			}
		}
		return (MissionManager) servletContext.getAttribute(MISSION_MANAGER_ATTRIBUTE_NAME);
	}


	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return 0;//INT_PARAMETER_ERROR;
	}
}

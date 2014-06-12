
package com.bbytes.config.client;

import com.ning.http.client.Response;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class HttpStatusUtil {

	
	public static final int NOT_FOUND = 404;
	
	public static final int INTERNAL_SERVER_ERROR = 500;
	
	public static final String SUCCESS = "success";

	public static final String RE_DIRECTION = "redirection";

	public static final String ERROR = "error";

	public static final String ERROR_INTERNAL = "error_internal";

	public static String getReponseStatus(Response response) {
		if (response.getStatusCode() / 100 == 2)
			return SUCCESS;

		if (response.getStatusCode() / 100 == 3)
			return RE_DIRECTION;

		if (response.getStatusCode() / 100 == 4)
			return ERROR;

		if (response.getStatusCode() / 100 == 5)
			return ERROR_INTERNAL;

		return ERROR;

	}
	
	public static boolean isSuccess(Response response) {
		return getReponseStatus(response).endsWith(HttpStatusUtil.SUCCESS);
	}
	
}

package com.bbytes.ccenter.web.filter.gzip;

import javax.servlet.ServletException;

public class GzipResponseHeadersNotModifiableException extends ServletException {

	private static final long serialVersionUID = 1400124000456516882L;

	public GzipResponseHeadersNotModifiableException(String message) {
        super(message);
    }
}

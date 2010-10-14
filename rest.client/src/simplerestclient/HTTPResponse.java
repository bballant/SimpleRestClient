/*******************************************************************************
 * Copyright (c) 2008, 2009 Brian Ballantine and Bug Labs, Inc.
 * 
 * MIT License
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package simplerestclient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;


/**
 * Class for wrapping the response connection.
 * This is returned when calling get/post/put/delete/head on an HttpRequest object
 * Use this object to check status code and get data from request
 * 
 * @author Brian
 * 
 * Revisions
 * 09-04-2008 AK added getHeaderField(String key) 
 *
 */
public class HTTPResponse {

	/////// we use the following response codes
	/**
	 * 200 general success
	 */
	public static final int HTTP_CODE_OK                 	= HttpURLConnection.HTTP_OK; // 200 general success
	/**
	 * 201 resource created
	 */
	public static final int HTTP_CODE_CREATED				= HttpURLConnection.HTTP_CREATED; // 201 resource created
	/**
	 * 400 general error
	 */
	public static final int HTTP_CODE_BAD_REQUEST         	= HttpURLConnection.HTTP_BAD_REQUEST; // 400 general error
	/**
	 * 401 not authorized
	 */
	public static final int HTTP_CODE_NOT_AUTHORIZED     	= HttpURLConnection.HTTP_UNAUTHORIZED; // 401 not authorized
	/**
	 * 404 not found
	 */
	public static final int HTTP_CODE_NOT_FOUND             = HttpURLConnection.HTTP_NOT_FOUND; // 404 not found
	
	/**
	 * 415 unsupported media type
	 */
	public static final int HTTP_CODE_UNSUPPORTED_TYPE		= HttpURLConnection.HTTP_UNSUPPORTED_TYPE; // 415 unsupported media type
	/**
	 * 500 internal/application error
	 */
	public static final int HTTP_CODE_INTERNAL_ERROR		= HttpURLConnection.HTTP_INTERNAL_ERROR; // 500 internal/application error
	
	
	private static final String DEFAULT_ERROR_MESSAGE = "There was a connection error.  The server responded with status code ";
	private HttpURLConnection _connection;
	private String responseData;
	
	/**
	 * constructor must take in an HttpURLConnection
	 */
	public HTTPResponse(HttpURLConnection connection) {
		_connection = connection;
	}
	
	
	/**
	 * Check the response status in http header
	 * throw error if an error status is returned
	 * 
	 * @throws IOException
	 * @throws HTTPException
	 */
	public void checkStatus() throws IOException, HTTPException {
		checkStatus(null);
	}
	
	
	/**
	 *  Get an input stream from the connection
	 *  
	 * @param connection
	 * @return InputStream body of HTTP Response
	 * @throws BugnetException 
	 * @Deprecated use getInputStream()
	 */
	public InputStream getStream() throws IOException, HTTPException {
		return getInputStream();
	}	

	/**
	 * Get the input stream from the connection.
	 *  
	 * @return
	 * @throws HTTPException
	 * @throws IOException
	 */
	public InputStream getInputStream() throws HTTPException, IOException {
		InputStream is = null;
		try {
			is = _connection.getInputStream();
		} catch (IOException e) {
			throwHTTPException(e);
		}
		return is;
	}


	/**
	 * Get a string from the connection
	 * 
	 * @param connection
	 * @return String body of HTTP Response
	 * @throws IOException 
	 * @Deprecated use readResponse()
	 */
	public String getString() throws IOException, HTTPException {
		return readResponse();
	}

	/**
	 * Read all bytes from response and load into a String.
	 * Safe to call multiple times.
	 * 
	 * @return
	 * @throws IOException
	 */
	public String readResponse() throws IOException {
		if (responseData == null) {
			InputStream is = getStream();
			responseData = inputStreamToString(is);
		}
		
		return responseData;
	}
	
	/**
	 * get response code from request
	 * 
	 * @param connection
	 * @return
	 */
	public int getResponseCode() {
    	String statusStr = _connection.getHeaderField("Status");
    	int status = 0;
    	if (statusStr != null)
    		status = Integer.parseInt(statusStr.substring(0,3));
    	return status;
	}
	
	/**
	 * Gets a header value from the http response
	 * 
	 * @param key
	 * @return
	 */
	public String getHeaderField(String key) {
		return _connection.getHeaderField(key);
	}
    

    /**
     * Get error message out of connection
     * 
     * @param connection
     * @return
     * @throws IOException 
     */
    public String getErrorMessage() throws IOException {
    	InputStream is = _connection.getErrorStream();
    	String errorStr = "";
    	if (is != null)
    		errorStr = inputStreamToString(is);
    	return errorStr;
    }
        
    
    /////////////////////////////////////////////////////////// Helpful Methods Club
	
    
	/**
	 * Check the status of the current connection and throw an error
	 * if we find an http error code.  Pass in a default error message.
	 * 
	 */
    private void checkStatus(String errorMessage) throws IOException, HTTPException {
		// Get Response code out
		int response = getResponseCode();
		// only set message from response body if it's an HTTP error
		if (response >= 400) {
			// gobble up the error so as not to hold us back getting an error message set
			try {
				errorMessage = getErrorMessage();
			} catch (IOException e) {
		    	if (errorMessage == null) errorMessage = DEFAULT_ERROR_MESSAGE + response + ".";
			}
			throw new HTTPException(response, errorMessage);
		}		
	}
    
    /**
     * Helper function deals w/ all http errors 
     * 
     */
	private void throwHTTPException(IOException ioexception) throws IOException, HTTPException {
		// Turn exception into HTTPException if possible
		if (_connection != null) {
			checkStatus(ioexception.getMessage());
		}
		throw ioexception;
	}    
    
    /**
     * convert input stream to string
     * 
     * @param is
     * @return
     * @throws IOException
     */
    private static String inputStreamToString(InputStream is) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line, resp = new String("");
		while ((line = rd.readLine()) != null) {
			resp = resp + line + "\n";
		}
		rd.close();
		return resp;
    }
}

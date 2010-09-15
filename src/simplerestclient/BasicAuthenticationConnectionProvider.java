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

import java.io.IOException;
import java.net.HttpURLConnection;


/**
 * A connection provider to use HTTP Basic Authentication
 * Create this and pass it into the HTTPRequest construction when creating it
 * 	to use basic authenticatioin:
 * 
 * HettpRequest req = HTTPRequest(new BasicAuthenticationConnectionProvider("foo", "bar"));
 * 
 * @author bballantine
 *
 */
public class BasicAuthenticationConnectionProvider extends DefaultConnectionProvider {

	String credentials;
	
	public BasicAuthenticationConnectionProvider(String username, String password) {
		String rawCreds = username + ":" + password;
		credentials = Base64.encodeBytes(rawCreds.getBytes());
	}

	public HttpURLConnection getConnection(String urlStr) throws IOException {
		HttpURLConnection connection = super.getConnection(urlStr);
		connection.setRequestProperty("Authorization", "Basic " + credentials);
		return connection;
	}

}

package simplerestclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A stateful HTTPRequest type that will limit requests at one per defined interval.
 * 
 * Useful for web service providers that have request rate limits.
 * 
 * @author kgilmer
 *
 */
public class RateLimitedHTTPRequest implements IHTTPRequest {

	private final int waitMillis;
	private HTTPRequest httpRequest;
	private Lock lock;

	/**
	 * @param waitMillis
	 */
	public RateLimitedHTTPRequest(int waitMillis) {
		this.waitMillis = waitMillis;	
		this.httpRequest = new HTTPRequest();
		lock = new ReentrantLock(true);
	}
	
	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#get(java.lang.String)
	 */
	@Override
	public HTTPResponse get(String url) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {		
			//System.out.println("Getting " + url + " at " + System.currentTimeMillis());
			return httpRequest.get(url);
		} finally {
			lock.unlock();
		}		
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#get(java.lang.String, java.util.Map)
	 */
	@Override
	public HTTPResponse get(String url, Map<String, String> headers) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.get(url, headers);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#post(java.lang.String, java.lang.String)
	 */
	@Override
	public HTTPResponse post(String url, String data) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.post(url, data);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#post(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public HTTPResponse post(String url, String data, Map headers) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.post(url, data, headers);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#post(java.lang.String, java.io.InputStream)
	 */
	@Override
	public HTTPResponse post(String url, InputStream stream) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.post(url, stream);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#post(java.lang.String, java.util.Map)
	 */
	@Override
	public HTTPResponse post(String url, Map properties) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.post(url, properties);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#post(java.lang.String, byte[])
	 */
	@Override
	public HTTPResponse post(String url, byte[] data) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.post(url, data);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#postMultipart(java.lang.String, java.util.Map)
	 */
	@Override
	public HTTPResponse postMultipart(String url, Map parameters) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.postMultipart(url, parameters);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#put(java.lang.String, java.lang.String)
	 */
	@Override
	public HTTPResponse put(String url, String data) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.put(url, data);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#put(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public HTTPResponse put(String url, String data, Map headers) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.put(url, data, headers);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#put(java.lang.String, java.io.InputStream)
	 */
	@Override
	public HTTPResponse put(String url, InputStream stream) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.put(url, stream);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#delete(java.lang.String)
	 */
	@Override
	public HTTPResponse delete(String url) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.delete(url);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#put(java.lang.String, java.util.Map)
	 */
	@Override
	public HTTPResponse put(String url, Map properties) throws IOException {
		if (!lockAndWait()) 
			return null;
		
		try {				
			return httpRequest.put(url, properties);
		} finally {
			lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see simplerestclient.IHTTPRequest#head(java.lang.String)
	 */
	@Override
	public HTTPResponse head(String url) throws IOException {
		if (!lockAndWait()) 
			return null;
				
		try {
			return httpRequest.head(url);
		} finally {			
			lock.unlock();
		}
	}
	
	public static void main(String[] args) {
		ExecutorService es = Executors.newFixedThreadPool(10);
		
		final IHTTPRequest hr = new RateLimitedHTTPRequest(2000);
		
		for (int i = 0; i < 20; ++i) {
			final int j = i;
			es.submit(new Runnable() {
				
				@Override
				public void run() {
					System.out.println("Task " + j + " started at " + System.currentTimeMillis());
					try {
						HTTPResponse resp = hr.get("http://yahoo.com/" + j);
						System.out.println("Task " + j + " ended at " + System.currentTimeMillis());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		
		es.shutdown();
	}
	

	/**
	 * Get the lock and sleep for predefined interval.
	 * @return
	 */
	private boolean lockAndWait() {
		try {
			lock.lock();
			Thread.sleep(waitMillis);
		} catch (InterruptedException e) {
			try {
				lock.unlock();
			} catch (IllegalMonitorStateException e2) {
				//Ignore
			}
			
			return false;
		}
		
		return true;
	}
}

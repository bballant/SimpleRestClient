package simplerestclient.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import simplerestclient.HTTPResponse;
import simplerestclient.SimpleHTTPRequest;

/**
 * Tests to exercise alternate APIs
 * @author kgilmer
 *
 */
public class NewRestClientAPITestCases extends TestCase {
	
	//Set this to whatever port your OSGi HTTP Service is running on.
	private static final int HTTP_SERVICE_PORT = 8095;
	
	/**
	 * Test a simple GET.
	 * @throws NamespaceException 
	 * @throws ServletException 
	 * @throws IOException 
	 */
	public void testHTTPGET() throws ServletException, NamespaceException, IOException {
		BundleContext context = Activator.getContext();
		TestServlet testServlet = new TestServlet();
		registerServlet(context, testServlet);
		
		HTTPResponse resp = SimpleHTTPRequest.get("http://localhost:" + HTTP_SERVICE_PORT + "/test");
		assertTrue(resp != null);
		assertTrue(resp.getErrorMessage().length() == 0);
		assertTrue(resp.getResponseCode() == 0);
		String content = resp.getString();
		assertTrue(content.trim().equals("viola"));
		
		assertTrue(testServlet.getCalled);
		assertFalse(testServlet.postCalled);
		assertFalse(testServlet.putCalled);
		assertFalse(testServlet.deleteCalled);
		
		unregisterServlet(context);
	}
	
	public void testHTTPPOST() throws ServletException, NamespaceException, IOException {
		BundleContext context = Activator.getContext();
		TestServlet testServlet = new TestServlet();
		registerServlet(context, testServlet);
		
		HTTPResponse resp = SimpleHTTPRequest.post("http://localhost:" + HTTP_SERVICE_PORT + "/test", "postdata");
		assertTrue(resp != null);
		assertTrue(resp.getErrorMessage().length() == 0);
		assertTrue(resp.getResponseCode() == 0);
		String content = resp.readResponse();
		assertTrue(content.trim().equals("viola"));
		//test reading multiple times
		content = resp.readResponse();
		assertTrue(content.trim().equals("viola"));
		
		assertFalse(testServlet.getCalled);
		assertTrue(testServlet.postCalled);
		assertFalse(testServlet.putCalled);
		assertFalse(testServlet.deleteCalled);
		
		unregisterServlet(context);
	}
	
	public void testHTTPPUT() throws ServletException, NamespaceException, IOException {
		BundleContext context = Activator.getContext();
		TestServlet testServlet = new TestServlet();
		registerServlet(context, testServlet);
		
		HTTPResponse resp = SimpleHTTPRequest.put("http://localhost:" + HTTP_SERVICE_PORT + "/test", "putdata");
		assertTrue(resp != null);
		assertTrue(resp.getErrorMessage().length() == 0);
		assertTrue(resp.getResponseCode() == 0);
		String content = resp.readResponse();
		assertTrue(content.trim().equals("viola"));
		
		assertFalse(testServlet.getCalled);
		assertFalse(testServlet.postCalled);
		assertTrue(testServlet.putCalled);
		assertFalse(testServlet.deleteCalled);
		
		unregisterServlet(context);
	}
	
	public void testHTTPDELETE() throws ServletException, NamespaceException, IOException {
		BundleContext context = Activator.getContext();
		TestServlet testServlet = new TestServlet();
		registerServlet(context, testServlet);
		
		HTTPResponse resp = SimpleHTTPRequest.delete("http://localhost:" + HTTP_SERVICE_PORT + "/test");
		assertTrue(resp != null);
		assertTrue(resp.getErrorMessage().length() == 0);
		assertTrue(resp.getResponseCode() == 0);
		String content = resp.readResponse();
		assertTrue(content.trim().equals("viola"));
		
		assertFalse(testServlet.getCalled);
		assertFalse(testServlet.postCalled);
		assertFalse(testServlet.putCalled);
		assertTrue(testServlet.deleteCalled);
		
		unregisterServlet(context);
	}
	
	

	private ServiceReference registerServlet(BundleContext context, TestServlet testServlet) throws ServletException, NamespaceException {
		ServiceReference sr = context.getServiceReference(HttpService.class.getName());
		
		assertTrue(sr != null);
		
		HttpService hs = (HttpService) context.getService(sr);
		
		hs.registerServlet("/test", testServlet, null, null);
		
		return sr;
	}
	
	private ServiceReference unregisterServlet(BundleContext context) throws ServletException, NamespaceException {
		ServiceReference sr = context.getServiceReference(HttpService.class.getName());
		
		assertTrue(sr != null);
		
		HttpService hs = (HttpService) context.getService(sr);
		
		assertTrue(hs != null);
		assertTrue(hs instanceof HttpService);
		
		hs.unregister("/test");
		
		return sr;
	}

	private class TestServlet extends HttpServlet {
		private boolean getCalled;
		private boolean postCalled;
		private boolean putCalled;
		private boolean deleteCalled;

		public TestServlet() {
			getCalled = false;
			postCalled = false;
			putCalled = false;
			deleteCalled = false;
		}

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			getCalled = true;
			resp.getWriter().write("viola");
		}
		
		@Override
		protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			deleteCalled = true;
			resp.getWriter().write("viola");
		}
		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			postCalled = true;
			resp.getWriter().write("viola");
		}
		
		@Override
		protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			putCalled = true;
			resp.getWriter().write("viola");
		}
	}
}

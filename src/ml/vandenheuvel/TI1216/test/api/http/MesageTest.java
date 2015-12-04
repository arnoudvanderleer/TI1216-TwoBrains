package ml.vandenheuvel.TI1216.test.api.http;

import static org.junit.Assert.*;

import org.junit.Test;

import ml.vandenheuvel.TI1216.source.api.http.Body;
import ml.vandenheuvel.TI1216.source.api.http.Header;
import ml.vandenheuvel.TI1216.source.api.http.Message;

public class MesageTest {

	@Test
	public void testConstructorHeader() {
		Header header = new Header();
		Body body = new Body("Content");
		Message testMessage = new Message(header, body);
		
		assertEquals(header, testMessage.getHeader());
	}
	
	@Test
	public void testConstructorBody() {
		Header header = new Header();
		Body body = new Body("Content");
		Message testMessage = new Message(header, body);
		
		assertEquals(body, testMessage.getBody());
	}
	
	@Test
	public void testGetHeader() {
		Header header = new Header();
		Header header2 = new Header();
		Body body = new Body("Content");
		Message testMessage = new Message(header, body);
		
		assertEquals(header2, testMessage.getHeader());
	}
	
	@Test
	public void testGetBody() {
		Header header = new Header();		
		Body body = new Body("Content");
		Body body2 = new Body("Content");
		Message testMessage = new Message(header, body);
		
		assertEquals(body2, testMessage.getBody());
	}
	
	@Test
	public void testSetHeader() {
		Header header = new Header();
		Header header2 = new Header();
		Body body = new Body("Content");
		Message testMessage = new Message(header, body);
		
		assertEquals(header, testMessage.getHeader());
		testMessage.setHeader(header2);
		//assertEquals(header2, testMessage.getHeader());
	}
	
	@Test 
	public void testSetBody() {
		Header header = new Header();
		Body body = new Body("Content");
		Body body2 = new Body("Content 2");
		Message testMessage = new Message(header, body);
		
		assertEquals(body, testMessage.getBody());
		testMessage.setBody(body2);
		assertEquals(body2, testMessage.getBody());
	}
}

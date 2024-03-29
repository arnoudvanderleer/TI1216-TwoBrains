package ml.vandenheuvel.ti1216.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import ml.vandenheuvel.ti1216.data.ChatMessage;
import ml.vandenheuvel.ti1216.data.Credentials;
import ml.vandenheuvel.ti1216.data.Faculty;
import ml.vandenheuvel.ti1216.data.Match;
import ml.vandenheuvel.ti1216.data.User;
import ml.vandenheuvel.ti1216.http.Body;
import ml.vandenheuvel.ti1216.http.Header;
import ml.vandenheuvel.ti1216.http.HeaderField;
import ml.vandenheuvel.ti1216.http.Message;
import ml.vandenheuvel.ti1216.http.RequestLine;
import ml.vandenheuvel.ti1216.http.ResponseLine;

/**
 * ServerCommunicator is a communicator class to communicate in a API-based way
 * with the server (from the client).
 */
public class ServerCommunicator {

	private static Logger logger = Logger.getLogger("ml.vandenheuvel.ti1216.client");

	private String targetAddress;
	private String targetHost;
	private int portNumber;

	public ServerCommunicator(String targetAddress, String targetHost, int portNumber) {
		this.targetAddress = targetAddress;
		this.targetHost = targetHost;
		this.portNumber = portNumber;
	}

	/**
	 * Tries to log in a user and fetches that user from the server.
	 * 
	 * @param credentials
	 *            the credentials to login with
	 * @return the user with specified credentials if present on the server,
	 *         otherwise null
	 */
	public JSONObject login(Credentials credentials) {
		logger.fine("Logging in with credentials " + credentials.toString() + "...");
		Message request = createMessage("get", "user", credentials);
		Message response = send(request);
		ResponseLine responseLine = (ResponseLine) response.getHeader().getHeaderLine();
		String statusCode = responseLine.getCode();
		if (!("200".equals(statusCode))) {
			logger.fine("Logging in not successful. Statuscode " + statusCode);
			return null;
		}
		logger.fine("Logging in successful.");
		JSONObject body = new JSONObject(response.getBody().getContent());
		if (body.getBoolean("success")) {
			return body;
		}
		return null;
	}

	/**
	 * Tries to register a user on the server.
	 * 
	 * @param credentials
	 *            the credentials to register with
	 * @param user
	 *            the user to register
	 * @return true if the user could be registered, otherwise false
	 */
	public boolean register(Credentials credentials, User user) {
		logger.fine("Registering user " + user.getUsername() + "...");
		Message request = createMessage("put", "user", credentials);
		request.getBody().setContent(user.toJSON().toString());
		Message response = send(request);
		ResponseLine responseLine = (ResponseLine) response.getHeader().getHeaderLine();
		String statusCode = responseLine.getCode();
		if (!("200".equals(statusCode))) {
			logger.fine("Registering  not successful. Statuscode " + statusCode);
			return false;
		}
		logger.fine("Registering successful.");
		JSONObject body = new JSONObject(response.getBody().getContent());
		return body.getBoolean("success");
	}

	/**
	 * Tries to update a user on the server.
	 * 
	 * @param credentials
	 *            the credentials of the user
	 * @param user
	 *            the user to update
	 * @return true if the user could be updated, otherwise false
	 */
	public boolean updateUser(Credentials credentials, User user) {
		logger.fine("Updating user " + user.getUsername() + "...");
		Message request = createMessage("update", "user", credentials);
		request.getBody().setContent(user.toJSON().toString());
		Message response = send(request);
		ResponseLine responseLine = (ResponseLine) response.getHeader().getHeaderLine();
		String statusCode = responseLine.getCode();
		if (!("200".equals(statusCode))) {
			logger.fine("Updating user not successful. Statuscode " + statusCode);
			return false;
		}
		logger.fine("Updating user successful.");
		JSONObject body = new JSONObject(response.getBody().getContent());
		return body.getBoolean("success");
	}

	/**
	 * Tries to send a chat message.
	 * 
	 * @param credentials
	 *            the credentials of the user
	 * @param chatMessage
	 *            the message to send
	 * @return true if the message could be sent, otherwise false
	 */
	public boolean sendChat(Credentials credentials, ChatMessage message) {
		logger.fine("Sending chat...");
		Message request = createMessage("put", "chat", credentials);
		request.getBody().setContent(message.toJSON().toString());
		Message response = send(request);
		ResponseLine responseLine = (ResponseLine) response.getHeader().getHeaderLine();
		String statusCode = responseLine.getCode();
		if (!("200".equals(statusCode))) {
			logger.fine("Sending chat not successful. Statuscode " + statusCode);
			return false;
		}
		JSONObject body = new JSONObject(response.getBody().getContent());
		return body.getBoolean("success");
	}

	/**
	 * Tries to fetch a faculty.
	 * 
	 * @param credentials
	 *            the credentials of the user
	 * @param facultyId
	 *            the id of the faculty to fetch
	 * @return a faculty if found, otherwise null
	 */
	public Faculty fetchFaculty(Credentials credentials, int facultyId) {
		logger.fine("Fetching faculty with id = " + facultyId + "...");
		Message request = createMessage("get", "faculty/" + facultyId, credentials);
		Message response = send(request);
		ResponseLine responseLine = (ResponseLine) response.getHeader().getHeaderLine();
		String statusCode = responseLine.getCode();
		if (!("200".equals(statusCode))) {
			logger.fine("Fetching faculty not successful. Statuscode: " + statusCode);
			return null;
		}
		logger.fine("Fetching faculty successful.");
		JSONObject body = new JSONObject(response.getBody().getContent());
		return Faculty.fromJSON(body.getJSONObject("faculty"));
	}

	/**
	 * Tries to fetch all faculties.
	 * 
	 * @param credentials
	 *            the credentials of the user
	 * @return a faculty if found, otherwise null
	 */
	public List<Faculty> fetchFaculties(Credentials credentials) {
		logger.fine("Fetching faculties...");
		Message request = createMessage("get", "faculty", credentials);
		Message response = send(request);
		ResponseLine responseLine = (ResponseLine) response.getHeader().getHeaderLine();
		String statusCode = responseLine.getCode();
		if (!("200".equals(statusCode))) {
			logger.fine("Fetching faculties not successful. Statuscode: " + statusCode);
			return new ArrayList<>();
		}
		JSONObject body = new JSONObject(response.getBody().getContent());
		ArrayList<Faculty> faculties = new ArrayList<>();
		JSONArray facultyData = body.getJSONArray("faculties");
		for (int i = 0; i < facultyData.length(); i++) {
			faculties.add(Faculty.fromJSON(facultyData.getJSONObject(i)));
		}
		logger.fine("Fetched " + facultyData.length() + "faculties.");
		return faculties;
	}

	/**
	 * Sends a HTTP message to the server and returns the response.
	 * 
	 * @param message
	 *            the message to send
	 * @return the response
	 */
	public Message send(Message message) {
		logger.finest("Sending message...");
		try {
			if (message.getHeader().getField("Host") == null)
				message.getHeader().addField(new HeaderField("Host", this.targetHost));
			logger.finest("Creating socket to " + this.targetAddress + " on port " + this.portNumber + "...");
			Socket socket = new Socket(this.targetAddress, this.portNumber);
			logger.finest("Created socket to " + this.targetAddress + " on port " + this.portNumber + ".");
			logger.finest("Creating inputStream...");
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());
			logger.finest("Created inputStream.");
			logger.finest("Creating printwriter over outputstream...");
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			logger.finest("Created printwriter over outputstream.");
			logger.finest("Writing message...");
			out.write(message.toString());
			logger.finest("Wrote message.");
			logger.finest("Flushing outputstream...");
			out.flush();
			logger.finest("Flushed outputstream.");
			logger.finest("Reading response...");
			Message result = Message.read(inputStream, false);
			logger.finest("Read response.");
			logger.finest("Closing socket...");
			socket.close();
			logger.finest("Closed socket.");
			out.close();
			return result;
		} catch (IOException e) {
			logger.log(Level.FINE, "Error while sending message and receiving response.", e);
		}
		return null;
	}

	/**
	 * Tries to fetch chat messages.
	 * 
	 * @param credentials
	 *            the credentials of the user
	 * @return an arrayList of chat messages if they could be fetched, otherwise
	 *         an empty arrayList
	 */
	public List<ChatMessage> fetchChats(Credentials credentials) {
		Message request = createMessage("get", "chat", credentials);
		Message response = send(request);
		ResponseLine responseLine = (ResponseLine) response.getHeader().getHeaderLine();
		String statusCode = responseLine.getCode();
		if (!("200".equals(statusCode))) {
			return new ArrayList<>();
		}
		JSONObject body = new JSONObject(response.getBody().getContent());
		JSONArray messages = body.getJSONArray("messages");
		ArrayList<ChatMessage> result = new ArrayList<>();
		for (int i = 0; i < messages.length(); i++) {
			result.add(ChatMessage.fromJSON(messages.getJSONObject(i)));
		}
		return result;
	}

	/**
	 * Tries to fetch matches
	 * 
	 * @param credentials
	 *            the credentials of the user
	 * @return an arrayList of matches if they could be fetched, otherwise an
	 *         empty arrayList
	 */
	public List<Match> fetchMatches(Credentials credentials) {
		Message request = createMessage("get", "match", credentials);
		Message response = send(request);
		ResponseLine responseLine = (ResponseLine) response.getHeader().getHeaderLine();
		String statusCode = responseLine.getCode();
		if (!("200".equals(statusCode))) {
			return new ArrayList<>();
		}
		JSONObject body = new JSONObject(response.getBody().getContent());
		JSONArray matches = body.getJSONArray("matches");
		ArrayList<Match> result = new ArrayList<>();
		for (int i = 0; i < matches.length(); i++) {
			result.add(Match.fromJSON(matches.getJSONObject(i)));
		}
		return result;
	}

	/**
	 * Tries to update a user on the server.
	 * 
	 * @param credentials
	 *            the credentials of the user
	 * @param match
	 *            the match to update
	 * @return true if the match could be updated, otherwise false
	 */
	public boolean updateMatch(Credentials credentials, Match match) {
		Message request = createMessage("update", "match", credentials);
		request.getBody().setContent(match.toJSON().toString());
		Message response = send(request);
		ResponseLine responseLine = (ResponseLine) response.getHeader().getHeaderLine();
		String statusCode = responseLine.getCode();
		if (!("200".equals(statusCode))) {
			return false;
		}
		JSONObject body = new JSONObject(response.getBody().getContent());
		return body.getBoolean("success");
	}

	/**
	 * Creates a default HTTP message with Basic authentication.
	 * 
	 * @param method
	 *            the HTTP method to use
	 * @param endpoint
	 *            the endpoint to request
	 * @param credentials
	 *            the credentials to authenticate with
	 * @return
	 */
	private static Message createMessage(String method, String endpoint, Credentials credentials) {
		Header header = new Header(new RequestLine(method.toUpperCase() + " /" + endpoint + " HTTP/1.1"));
		Message result = new Message(header, new Body(""));
		result.getHeader().addField(new HeaderField("Authorization", "Basic " + Base64.getEncoder()
				.encodeToString((credentials.getUsername() + ":" + credentials.getPassword()).getBytes())));
		return result;
	}

}

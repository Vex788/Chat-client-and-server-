package ua.kiev.prog;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MessageList {
	private UsersList usersList = UsersList.getInstance();
	private static final MessageList msgList = new MessageList();

    private final Gson gson;
	private List<Message> list = new LinkedList<>();
	
	public static MessageList getInstance() {
		return msgList;
	}
  
  	private MessageList() {
		gson = new GsonBuilder().create();

		try {
			BufferedReader br = new BufferedReader(new FileReader("MessageList.json"));
			Type itemsListType = new TypeToken<List<Message>>() {}.getType();
			list = new Gson().fromJson(br.readLine(), itemsListType);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean saveData() {
		try (Writer writer = new FileWriter("MessageList.json")) {
			gson.toJson(list, writer);

			return true;
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}
	}
	
	public synchronized void add(Message m) {
		if (list == null) list = new LinkedList<>();
		list.add(m);
		System.out.println(m);
	}
	
	public synchronized String toJSON(int n) {
		if (list != null) {
			if (n >= list.size()) return null;
		}
		return gson.toJson(new JsonMessages(list, n));
	}

	public synchronized String[] sendMessageToAllGroupMembers(String[] groupNameAndMembers, String fromLogin, String messageText) { // groupName m1 m2 m3
		for (int i = 1; i < groupNameAndMembers.length; i++) {
			Message message1 = new Message(groupNameAndMembers[0], fromLogin + ", " + messageText);
			message1.setTo(groupNameAndMembers[i]);
			System.out.println(message1.getText() + "-----");
			list.add(message1);
		}

		return null;
	}
}

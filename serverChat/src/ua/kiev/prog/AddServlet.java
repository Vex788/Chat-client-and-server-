package ua.kiev.prog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AddServlet extends HttpServlet {

	private MessageList msgList = MessageList.getInstance();
	private UsersList usersList = UsersList.getInstance();

	protected Message parseMessage(Message message) {
        if (usersList.checkLoginOnlineStatus(message.getFrom())) { // if user online parse command
            if (message.getText().contains("@")) { // command @Member some text...
                String login = message.getText().split(" ")[0].replace("@", "");
                boolean loginExists = usersList.checkLoginExists(login);

                message.setFrom(message.getFrom());
                if (loginExists) {
                    message.setTo(login);
                    message.setText(message.getText().substring(message.getText().indexOf(" ") + 1));
                } else {
                    message.setTo(message.getFrom());
                    message.setText("login not found");
                }

                return message;
            } else if (message.getText().equals("/save_all_data")) { // command /save_all_data
                boolean ul = usersList.saveData();
                boolean ml = msgList.saveData();

                message.setFrom("System");

                if (ul && ml) {
                    message.setTo(message.getFrom());
                    message.setText("All data saved");
                } else {
                    message.setTo(message.getFrom());
                    message.setText("Data saved error");
                }
            } else if (message.getText().equals("/logout")) { // command /logout
                usersList.setStatus(message.getFrom(), "offline");
                message.setTo(message.getFrom());
                message.setText(message.getFrom() + " offline");
                message.setFrom("System");
            } else if (message.getText().contains("/show_ulist")) { // command /show_ulist
                message.setTo(message.getFrom());
                message.setText("\r\n" + usersList.toString());
            } else if (message.getText().contains("/cg ")) { // command /cg groupName m1 m2 m3
                String[] temp = message.getText().split(" ");

                if (!usersList.checkGroupExists(temp[1])) { // if group name isn't taken
                    // get group members
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 2; i < temp.length; i++) {
                        stringBuilder.append(temp[i] + " ");
                    }
                    // set group
                    usersList.setGroupNameAndMembers(stringBuilder.toString().split(" "), temp[1]);
                    // send message to all members
                    String[] groupNameAndMembers = usersList.getGroupNameAndMembers(temp[1]).split(" ");

                    for (int i = 1; i < groupNameAndMembers.length; i++) {
                        Message m = new Message(groupNameAndMembers[0], "You are in group");
                        m.setTo(groupNameAndMembers[i]);

                        msgList.add(m);
                    }

                    message.setFrom("System");
                    message.setTo(temp[1]);
                    message.setText(temp[1] + " created");
                } else {
                    message.setTo(message.getFrom());
                    message.setFrom("System");
                    message.setText(temp[1] + " name is taken");
                }
            } else if (message.getText().contains("/gm ")) { // command /gm groupName some text...
                String[] temp = message.getText().split(" ");

                if (usersList.checkGroupExists(temp[1])) { // if group name is taken
                    if (usersList.checkLoginExistsInGroup(message.getFrom(), temp[1])) {
                        // get message for group
                        StringBuilder stringBuilder = new StringBuilder();

                        for (int i = 2; i < temp.length; i++) {
                            stringBuilder.append(temp[i] + " ");
                        }
                        // send message to all members
                        String[] groupNameAndMembers = usersList.getGroupNameAndMembers(temp[1]).split(" ");

                        for (int i = 1; i < groupNameAndMembers.length; i++) {
                            Message m = new Message(groupNameAndMembers[0],
                                    message.getFrom() + "- " + stringBuilder.toString());

                            m.setTo(groupNameAndMembers[i]);

                            msgList.add(m);
                        }

                        return null;
                    } else {
                        message.setTo(message.getFrom());
                        message.setFrom("System");
                        message.setText("You are not invited to this group");
                    }
                } else {
                    message.setTo(message.getFrom());
                    message.setFrom("System");
                    message.setText(temp[1] + " not found");
                }
            }
        }

        return message;
    }

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		byte[] buf = requestBodyToArray(req);
        String bufStr = new String(buf, StandardCharsets.UTF_8);

		Message msg = Message.fromJSON(bufStr);
		if (msg != null) {
		    msg = parseMessage(msg);

		    if (msg != null) {
                msgList.add(msg);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
	}

	private byte[] requestBodyToArray(HttpServletRequest req) throws IOException {
        InputStream is = req.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
}

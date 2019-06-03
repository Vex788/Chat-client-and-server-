import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {

	private static Login login = Login.getInstance();

	private static final String COMMAND_LIST =
			"/login username password          - login\r\n" +
			"/register username password       - register\r\n" +
			"@Username message text            - private message\r\n" +
			"/cg group_name member1 member2... - create group\r\n" +
			"/gm group_name message text       - group message\r\n" +
			"/save_all_data                    - save all data on server\r\n" +
			"/show_ulist                       - show users list\r\n" +
			"/logout                           - logout";

	public  static Message parseMessage(Message message) {
		if (message.getText().contains("/login ") ||
			message.getText().contains("/register ") ||
			message.getText().contains("/cg ") ||
			message.getText().contains("/gm ")) {
			String[] temp = message.getText().split(" ");

			if (temp.length < 3) {
				System.out.println("System> Invalid command\r\nEnter /help - for getting command list");

				return null;
			} else {
                if (message.getText().contains("/login ")) {
					login.LogIn(temp[1], temp[2]);

					message = null;
				} else if (message.getText().contains("/register ")) {
                	login.register(temp[1], temp[2]);

                	message = null;
				}
            }
		} else if (message.getText().equals("/help")) {
			System.out.println(COMMAND_LIST);
			message = null;
		} else if (message.getText().trim().equals("/save_all_data")) {
			return message;
		} else if (message.getText().trim().equals("/logout")) {
			System.out.println("System> " + login.getLogin() + " offline");
			login.setLogin("Guest");
		} else if (message.getText().trim().equals("/show_ulist")) {
			message = null;
		} else if (message.getText().contains("/")) {
			System.out.println("System> Invalid command\r\nEnter /help - for getting command list");
			message = null;
		}

		return message;
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			Thread th = new Thread(new GetThread());
			th.setDaemon(true);
			th.start();

			System.out.println("          -=<Chatizer>=-");

			while (true) {

				String command = scanner.nextLine().trim();

				if (command.isEmpty()) { break; }

				Message m = parseMessage(new Message(login.getLogin(), command)); // parse message

				if (m != null) {
					int res = m.send(Utils.getURL() + "/add"); // server answer

					if (res != 200) { // 200 OK
						System.out.println("HTTP error occured: " + res);
						return;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
	}
}

package ua.kiev.prog;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LoginServlet extends HttpServlet {
    private UsersList usersList = UsersList.getInstance();
    private MessageList msgList = MessageList.getInstance();

    protected Message parseMessage(String login, String password) {
        Message message = new Message("System", "NULL");

        if (true) {//!usersList.checkLoginOnlineStatus(login)) { // if user offline
                boolean loginEx = usersList.checkLoginExists(login);
                boolean passwordEx = usersList.checkPasswordsEquals(login, password);

                message.setTo(login);

                if (loginEx) {
                    if (passwordEx) {
                        usersList.setStatus(login, "online");

                        message.setFrom("System");
                        message.setText("/login_success " + login + " online");
                    } else {
                        message.setText("/login_error passwords don't match");
                    }
                } else {
                    message.setText("/login_error login not exists");
                }
        }
        System.out.println(message.getText());
        return message;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        Message message = null;

        if (login != null && password != null) {
            message = parseMessage(login, password);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.setContentType("application/json");

        if (message != null) {
            String json = message.toJSON();

            if (json != null) {
                OutputStream os = resp.getOutputStream();
                byte[] buf = json.getBytes(StandardCharsets.UTF_8);
                os.write(buf);
            }
        }
    }
}

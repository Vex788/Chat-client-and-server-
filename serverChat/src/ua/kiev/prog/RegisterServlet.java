package ua.kiev.prog;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RegisterServlet extends HttpServlet {
    private UsersList usersList = UsersList.getInstance();

    protected Message parseMessage(String login, String password) {
        Message message = new Message("System", "NULL");
        message.setTo(login);

        boolean loginEx = usersList.checkLoginExists(login);

        if (!loginEx) {
            message.setText("/register_success " + login + " registered");

            usersList.add(new User(login, password, "offline"));
        } else {
            message.setText("/register_error login exists");
        }
        System.out.println(message.getText());
        return message;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        if (message != null) {System.out.println("wowowowowow");
            String json = message.toJSON();

            if (json != null) {
                OutputStream os = resp.getOutputStream();
                byte[] buf = json.getBytes(StandardCharsets.UTF_8);
                os.write(buf);
            }
        }
    }
}

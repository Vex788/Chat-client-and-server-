import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class Login {
    private String login = "Guest";
    private int counter = 0;

    private static final Login cLogin = new Login();

    private final List<Message> list = new LinkedList<>();

    public static Login getInstance() {
        return cLogin;
    }

    private Login() {

    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public boolean LogIn(String login, String password) {
        try {
            Gson gson = new GsonBuilder().create();
            URL url = new URL(Utils.getURL() + "/login?login=" + login + "&password=" + password);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream is = http.getInputStream();
            try {
                byte[] buf = requestBodyToArray(is);
                String strBuf = new String(buf, StandardCharsets.UTF_8);

                Message message = gson.fromJson(strBuf, Message.class);

                if (message != null) {
                    if (message.getText().contains("/login_success ")) {
                        this.login = login;
                        this.counter = 0;
                        System.out.println("System> " + login + " online");
                        return true;
                    } else if (message.getText().contains("/login_error ")) {
                        this.login = "Guest";
                        System.out.println("System> " +
                                message.getText().substring(message.getText().indexOf(" ") + 1));
                        return true;
                    }
                }
            } finally {
                is.close();
            }
        } catch (IOException muEx) {
            muEx.fillInStackTrace();
        }

        return false;
    }

    public boolean register(String login, String password) {
        try {
            Gson gson = new GsonBuilder().create();
            URL url = new URL(Utils.getURL() + "/register?login=" + login + "&password=" + password);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream is = http.getInputStream();
            try {
                byte[] buf = requestBodyToArray(is);
                String strBuf = new String(buf, StandardCharsets.UTF_8);

                Message message = gson.fromJson(strBuf, Message.class);

                if (message != null) {
                    if (message.getText().contains("/register_success ")) {
                        this.login = login;
                        System.out.println("System> " + login + " registered");
                        return true;
                    } else if (message.getText().contains("/register_error ")) {
                        this.login = "Guest";
                        System.out.println("System> " +
                                message.getText().substring(message.getText().indexOf(" ") + 1));
                        return true;
                    }
                }
            } finally {
                is.close();
            }
        } catch (IOException muEx) {
            muEx.fillInStackTrace();
        }

        return false;
    }

    private byte[] requestBodyToArray(InputStream is) throws IOException {
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

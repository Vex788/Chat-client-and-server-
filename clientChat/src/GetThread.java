import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetThread implements Runnable {
    private Login login = Login.getInstance();
    private final Gson gson;

    public GetThread() {
        gson = new GsonBuilder().create();
    }

    @Override
    public void run() {
        try {
            while ( ! Thread.interrupted()) {
                URL url = new URL(Utils.getURL() + "/get?from=" + login.getCounter());
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream is = http.getInputStream();

                try {
                    byte[] buf = requestBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);

                    JsonMessages list = gson.fromJson(strBuf, JsonMessages.class);

                    if (list != null) {
                        for (Message m : list.getList()) {
                            try {
                                if (!m.getFrom().equals("System") && !m.getText().contains(" offline")) {
                                    if (m.getFrom().equals(login.getLogin()) ||
                                            m.getTo().equals(login.getLogin()) ||
                                            m.getFrom().equals("public") ||
                                            m.getFrom().equals("System")) {
                                        System.out.println(m.toString());
                                    }
                                }
                            } catch (NullPointerException e) {
                                // e.printStackTrace();
                            }
                            login.setCounter(login.getCounter() + 1);
                        }
                    }
                } finally {
                    is.close();
                }

                Thread.sleep(1000);
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } catch (InterruptedException iEx) {
            iEx.printStackTrace();
        }
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

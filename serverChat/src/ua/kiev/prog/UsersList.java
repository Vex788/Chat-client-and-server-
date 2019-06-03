package ua.kiev.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class UsersList {

    private static final UsersList usersList = new UsersList();

    private final Gson gson;
    private List<User> list = new LinkedList<>();

    public static UsersList getInstance() {
        return usersList;
    }

    private UsersList() {
        gson = new GsonBuilder().create();

        try {
            BufferedReader br = new BufferedReader(new FileReader("UsersList.json"));
            Type itemsListType = new TypeToken<List<User>>() {}.getType();
            list = new Gson().fromJson(br.readLine(), itemsListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void add(User user) {
        list.add(user);
        System.out.println(user.getLogin() + " write in list");
        saveData();
    }

    public synchronized String getGroupNameAndMembers(String groupName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(groupName + " ");

        for (User user : list) {
            if (user.getGroups() != null) {
                if (user.getGroups().contains(groupName)) {
                    stringBuilder.append(user.getLogin() + " ");
                }
            }
        }

        return stringBuilder.toString();
    }

    public synchronized void setGroupNameAndMembers(String[] logins, String groupName) {
        for (User user : list) {
            for (String login : logins) {
                if (user.getLogin().equals(login)) {
                    user.setGroups(user.getGroups() + " " + groupName);
                    System.out.println(user.getLogin() + " " + user.getStatus() + " " + user.getGroups());
                }
            }
        }
    }

    public synchronized boolean checkLoginExistsInGroup(String login, String groupName) {
        for (User user : list) {
            if (user.getGroups() != null) {
                if (user.getGroups().contains(groupName) && user.getLogin().equals(login)) {
                    return true;
                }
            }
        }

        return false;
    }

    public synchronized boolean checkGroupExists(String groupName) {
        for (User user : list) {
            if (user.getGroups() != null) {
                if (user.getGroups().contains(groupName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public synchronized boolean saveData() {
        try (Writer writer = new FileWriter("UsersList.json")) {
            gson.toJson(list, writer);

            return true;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (User user : list) {
            stringBuilder.append(user.getLogin() + " - " + user.getStatus() + "\r\n");
        }

        if (list.isEmpty()) {
            stringBuilder.append("list is empty");
        }

        return stringBuilder.toString();
    }

    public synchronized String setStatus(String login, String status) {
        for (User user : list) {
            if (user.getLogin().equals(login)){
                user.setStatus(status);
                return status;
            }
        }
        return null;
    }

    public synchronized boolean checkPasswordsEquals(String login, String password) {
        for (User user : list) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

    public synchronized boolean checkLoginExists(String login) {
        for (User user : list) {
            if (user.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public synchronized boolean checkLoginOnlineStatus(String login) {
        for (User user : list) {
            if (user.getLogin().equals(login)) {
                if (user.getStatus() == "online") {
                    return true;
                }
            }
        }

        return false;
    }
}

package server;

import java.util.ArrayList;
import java.util.List;

public class SQLiteAuthService implements AuthService {

    private List<UserData> users;

    public SQLiteAuthService() {
        fillUserList();
    }

    @Override
    public UserData getUserByLoginAndPassword(String login, String password) {
        for (UserData o:users ) {
            if(o.getLogin().equals(login) && o.getPassword().equals(password)){
                return o;
            }
        }

        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        for (UserData o:users ) {
            if(o.getLogin().equals(login)) {
                return false;
            }
        }
        Long newUserId = DBTools.createUser(login, password, nickname);
        users.add(new UserData(newUserId, login, password, nickname));
        return true;
    }

    @Override
    public void fillUserList() {
        this.users = DBTools.getUserList();
    }

    @Override
    public boolean changeNick(ClientHandler client, String newNick) {
        return DBTools.changeNick(client.getUserId(), newNick);
    }
}


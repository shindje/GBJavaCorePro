package server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService {

    private List<UserData> users;

    public SimpleAuthService() {
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
        users.add(new UserData(login, password, nickname));
        return true;
    }

    @Override
    public void fillUserList() {
        this.users = new ArrayList<>();

        for (int i = 1; i <= 10 ; i++) {
            users.add(new UserData("login"+i, "pass"+i, "nick"+i));
        }

        for (int i = 1; i <= 3 ; i++) {
            users.add(new UserData(""+i, ""+i, "simple_nick"+i));
        }
    }

    @Override
    public boolean changeNick(ClientHandler client, String newNick) {
        return false;
    }


}

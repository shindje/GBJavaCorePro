package server;

public interface AuthService {
    UserData getUserByLoginAndPassword(String login, String password);
    boolean registration(String login, String password, String nickname);
    void fillUserList();
    boolean changeNick(ClientHandler client, String newNick);
}

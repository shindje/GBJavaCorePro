package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBTools {
    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement psChangeNick;
    private static PreparedStatement psCheckNick;
    private static PreparedStatement psAddToHistory;
    private static PreparedStatement psGetHistory;
    private static PreparedStatement psCreateUser;

    static void getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null || connection.isClosed()) {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            stmt = connection.createStatement();
            psChangeNick = connection.prepareStatement("UPDATE users SET nickname = ? WHERE id = ?");
            psCheckNick = connection.prepareStatement("SELECT login FROM users WHERE nickname = ? AND id != ?");
            psAddToHistory = connection.prepareStatement(
                    "INSERT INTO chat_history(send_user_id, to_user_id, message) VALUES(?, ?, ?)");
            psGetHistory = connection.prepareStatement(
                    "SELECT * FROM (" +
                        "SELECT send_user.nickname as send_nick, to_user.nickname as to_nick, h.message, h.id " +
                        "FROM chat_history h " +
                        "JOIN users send_user on send_user.id = h.send_user_id " +
                        "LEFT JOIN users to_user on to_user.id = h.to_user_id " +
                        "WHERE to_user_id is null OR send_user_id = ? OR to_user_id = ? " +
                        "ORDER BY h.id DESC " +
                        "LIMIT ? " +
                    ") " +
                    "ORDER BY id");
            psCreateUser = connection.prepareStatement(
                    "INSERT INTO users(login, password, nickname) " +
                    "VALUES(?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
        }
    }

    static void disconnect() {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            psChangeNick.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            psCheckNick.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            psAddToHistory.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            psGetHistory.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            psCreateUser.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static List<UserData> getUserList() {
        List<UserData> users = new ArrayList<>();
        try {
            getConnection();
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM users");) {
                while (rs.next()) {
                    UserData user = new UserData(
                            rs.getLong("id"),
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("nickname"));
                    users.add(user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            return users;
        }
    }

    static boolean changeNick(Long id, String newNickName) {
        try {
            getConnection();
            psCheckNick.setString(1, newNickName);
            psCheckNick.setLong(2, id);
            try (ResultSet rs = psCheckNick.executeQuery()) {
                if (rs.next()) {
                    return false;
                } else {
                    psChangeNick.setString(1, newNickName);
                    psChangeNick.setLong(2, id);
                    psChangeNick.executeUpdate();
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
        }
    }

    static void addToHistory(Long sendUserId, Long toUserId, String message) {
        try {
            getConnection();
            psAddToHistory.setLong(1, sendUserId);
            if (toUserId == null) {
                psAddToHistory.setNull(2, Types.INTEGER);
            } else {
                psAddToHistory.setLong(2, toUserId);
            }
            psAddToHistory.setString(3, message);
            psAddToHistory.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static List<String> getHistory(Long userId, int limit) {
        List<String> history = new ArrayList<>();
        try {
            getConnection();
            psGetHistory.setLong(1, userId);
            psGetHistory.setLong(2, userId);
            psGetHistory.setInt(3, limit);
            try (ResultSet rs = psGetHistory.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString("to_nick") == null) {
                        history.add(rs.getString("send_nick") + ": " + rs.getString("message"));
                    } else {
                        history.add(String.format("[ %s ] private [ %s ] : %s",
                                rs.getString("send_nick"), rs.getString("to_nick"),
                                rs.getString("message")));
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return history;
    }

    static Long createUser(String login, String password, String nickname) {
        Long newId = null;
        try {
            getConnection();
            psCreateUser.setString(1, login);
            psCreateUser.setString(2, password);
            psCreateUser.setString(3, nickname);
            psCreateUser.executeUpdate();
            try (ResultSet rs = psCreateUser.getGeneratedKeys()) {
                if (rs.next()) {
                    newId = rs.getLong(1);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newId;
    }
}

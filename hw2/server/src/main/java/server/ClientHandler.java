package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.*;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private UserData user = new UserData();
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Server server, Socket socket) {
        if (logger.getHandlers().length == 0)
            server.prepareLogger(logger);

        this.server = server;
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            server.addTask(() -> {
                try {
                    //Если в течении 5 секунд не будет сообщений по сокету то вызовится исключение
                    socket.setSoTimeout(3000);

                    //цикл аутентификации
                    while (true) {
                        String str = in.readUTF();
                        logger.fine("message [" + socket.getRemoteSocketAddress() + "]: " + str);

                        if (str.startsWith("/reg ")) {
                            String[] token = str.split(" ");

                            if (token.length < 4) {
                                continue;
                            }

                            boolean succeed = server
                                    .getAuthService()
                                    .registration(token[1], token[2], token[3]);
                            if (succeed) {
                                sendMsg("Регистрация прошла успешно");
                                logger.info("reg ok: " + token[1]);
                            } else {
                                sendMsg("Регистрация  не удалась. \n" +
                                        "Возможно логин уже занят, или данные содержат пробел");
                                logger.info("reg fail: " + token[1] + ", " + token[2] + ", " + token[3]);
                            }
                        }

                        if (str.startsWith("/auth ")) {
                            String[] token = str.split(" ");

                            if (token.length < 3) {
                                continue;
                            }

                            user = server.getAuthService()
                                    .getUserByLoginAndPassword(token[1], token[2]);

                            if (user != null && user.getNickname() != null) {
                                if (!server.isLoginAuthorized(user.getLogin())) {
                                    sendMsg("/authok " + user.getNickname());
                                    server.subscribe(this);
                                    logger.info("authok " + user);
                                    socket.setSoTimeout(0);
                                    break;
                                } else {
                                    sendMsg("С этим логином уже прошли аутентификацию");
                                    logger.info("auth fail1 : " + token[1] + ", " + token[2]);
                                }
                            } else {
                                sendMsg("Неверный логин / пароль");
                                logger.info("auth fail2 : " + token[1] + ", " + token[2]);
                            }
                        }
                    }

                    //цикл работы
                    while (true) {
                        String str = in.readUTF();
                        logger.fine("message [" + user + "]: " + str);

                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                sendMsg("/end");
                                break;
                            }
                            if (str.startsWith("/w ")) {
                                String[] token = str.split(" ", 3);

                                if (token.length < 3) {
                                    continue;
                                }

                                server.privateMsg(this, token[1], token[2]);
                            }
                            if (str.startsWith("/nick ")) {
                                String[] token = str.split(" ");
                                if (token.length < 2) {
                                    sendMsg("Введите новый ник");
                                } else {
                                    String newNick = token[1];
                                    boolean res = server.changeNick(this, newNick);
                                    if (res) {
                                        sendMsg("Ваш ник изменен на " + newNick);
                                    } else {
                                        sendMsg("Ошибка изменения ника");
                                    }

                                }
                            }
                            if (str.startsWith("/history ")) {
                                String[] token = str.split(" ");
                                server.sendHistory(this, Integer.parseInt(token[1]));
                            }
                        } else {
                            server.broadcastMsg(user, str);
                        }
                    }
                }catch (SocketTimeoutException e){
                    sendMsg("/end");
                    logger.warning("SocketTimeoutException: " + e.getMessage());
                }
                ///////
                catch (IOException e) {
                    e.printStackTrace();
                    logger.warning("IOException во время работы: " + e.getMessage());
                } finally {
                    server.unsubscribe(this);
                    logger.info("Клиент отключился " + socket.getRemoteSocketAddress());
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.warning("IOException при отключении: " + e.getMessage());
                    }
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("IOException: " + e.getMessage());
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return user.getNickname();
    }

    public void setNick(String nick) {
        user.setNickname(nick);
    }

    public String getLogin() {
        return user.getLogin();
    }

    public Long getUserId() {
        return user.getId();
    }

    public UserData getUser() {
        return user;
    }
}

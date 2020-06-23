package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private UserData user = new UserData();

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //Если в течении 5 секунд не будет сообщений по сокету то вызовится исключение
                    socket.setSoTimeout(3000);

                    //цикл аутентификации
                    while (true) {
                        String str = in.readUTF();

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
                            } else {
                                sendMsg("Регистрация  не удалась. \n" +
                                        "Возможно логин уже занят, или данные содержат пробел");
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
                                    System.out.println("Клиент: " + user.getNickname() + " подключился"+ socket.getRemoteSocketAddress());
                                    socket.setSoTimeout(0);
                                    break;
                                } else {
                                    sendMsg("С этим логином уже прошли аутентификацию");
                                }
                            } else {
                                sendMsg("Неверный логин / пароль");
                            }
                        }
                    }

                    //цикл работы
                    while (true) {
                        String str = in.readUTF();

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
                        } else {
                            server.broadcastMsg(user, str);
                        }
                    }
                }catch (SocketTimeoutException e){
                    sendMsg("/end");
                }
                ///////
                catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    System.out.println("Клиент отключился");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
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
}

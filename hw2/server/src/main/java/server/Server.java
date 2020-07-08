package server;


import org.sqlite.core.DB;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;


public class Server {
    private List<ClientHandler> clients;
    private AuthService authService;
    private ExecutorService executor;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static FileHandler fileHdlr;

    static void prepareLogger(Logger logger) {
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                return sdf.format(new Date(record.getMillis())) + " " + record.getLoggerName()  + " "
                        + record.getLevel() + ": " + record.getMessage() + "\n";
            }
        };
        Handler consoleHdlr = new ConsoleHandler();
        consoleHdlr.setLevel(Level.ALL);
        consoleHdlr.setFormatter(formatter);
        logger.addHandler(consoleHdlr);
        try {
            if (fileHdlr == null) {
                fileHdlr = new FileHandler("log.txt", true);
                fileHdlr.setLevel(Level.INFO);
                fileHdlr.setFormatter(formatter);
            }
            logger.addHandler(fileHdlr);
        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("IOException при создании FileHandler: " + e.getMessage());
        }
    }

    public Server() {
        prepareLogger(logger);
        clients = new Vector<>();
        authService = new SQLiteAuthService();
        //SingleThreadExecutor и FixedThreadPool не подходят, т.к. все клиенты должны обрабатываться одновременно
        //Использование ExecutorService оправдано, если клиенты будут отключаться и подключаться.
        //При подключении нового клиента будет взят поток из пула, если какой-нибудь клиент отключился
        executor = Executors.newCachedThreadPool();
        ServerSocket server = null;
        Socket socket;

        final int PORT = 8189;

        try {
            server = new ServerSocket(PORT);
            logger.info("Сервер запущен!");

            while (true) {
                socket = server.accept();
                logger.info("Клиент подключился " + socket.getRemoteSocketAddress());
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("Ошибка при запуске: " + e.getMessage());
        } finally {
            executor.shutdown();
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.warning("Ошибка при остановке: " + e.getMessage());
            }
            logger.info("Сервер остановлен!");
        }
    }

    public void broadcastMsg(UserData user, String msg) {
        logger.fine("broadcastMsg from [" + user + "]: " + msg);
        for (ClientHandler c : clients) {
            c.sendMsg(user.getNickname() + ": " + msg);
        }
        DBTools.addToHistory(user.getId(), null, msg);
    }

    public void privateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[ %s ] private [ %s ] : %s",
                sender.getNick(), receiver, msg);
        logger.fine("privateMsg: " + message);

        for (ClientHandler c : clients) {
            if (c.getNick().equals(receiver)) {
                c.sendMsg(message);
                if (!sender.getNick().equals(receiver)) {
                    sender.sendMsg(message);
                }
                DBTools.addToHistory(sender.getUserId(), c.getUserId(), msg);
                return;
            }
        }

        sender.sendMsg("not found user: " + receiver);
    }


    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void sendHistory(ClientHandler clientHandler, int limit) {
        List<String> history = DBTools.getHistory(clientHandler.getUserId(), limit);
        for (String str: history) {
            clientHandler.sendMsg(str);
        }
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isLoginAuthorized(String login){
        for (ClientHandler c : clients) {
            if (c.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    private void broadcastClientList() {
        logger.fine("broadcastClientList");
        StringBuilder sb = new StringBuilder("/clientlist ");

        for (ClientHandler c : clients) {
            sb.append(c.getNick()).append(" ");
        }
        String msg = sb.toString();

        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }

    boolean changeNick(ClientHandler client, String newNick) {
        logger.info("changeNick [" + client.getUser() + "]: " + newNick);
        boolean res = authService.changeNick(client, newNick);
        if (res) {
            authService.fillUserList();
            client.setNick(newNick);
            broadcastClientList();
        }
        return res;
    }

    public void addTask(Runnable r) {
        executor.execute(r);
        System.out.println("Taks added " + executor.toString());
    }
}

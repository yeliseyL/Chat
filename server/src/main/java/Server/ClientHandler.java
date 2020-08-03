package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {
    Server server;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    private String nickname;
    private String login;
    private String password;
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            executorService.execute(() -> {
                try {
                    //auth cycle
                    socket.setSoTimeout(120_000);
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/auth ")) {
                            String[] token = str.split("\\s");
                            if(token.length < 3) {
                                continue;
                            }
                            String newNick = server
                                    .getAuthService()
                                    .getNicknameByLoginAndPassword(token[1], token[2]);
                            login = token[1];
                            password = token[2];

                            if (newNick != null) {
                                if (!server.isLoginAuthorized(login)) {
                                    sendMessage("/authok " + newNick + " " + login);
                                    nickname = newNick;
                                    server.subscribe(this);
                                    logger.log(Level.INFO, String.format("Client %s connected %n", nickname));
                                    break;
                                } else {
                                    logger.log(Level.INFO, "This login has been already authorized.");
                                    sendMessage("This login has been already authorized.");
                                }
                            } else {
                                logger.log(Level.INFO, "Wrong login or password!\n");
                                sendMessage("Wrong login or password!\n");
                            }
                        }
                        if (str.startsWith("/reg ")) {
                            String[] token = str.split("\\s");
                            if(token.length < 4) {
                                continue;
                            }
                            boolean b = server.getAuthService().registration(token[1], token[2], token[3]);
                            if (b) {
                                sendMessage("/regresult ok");
                            } else {
                                sendMessage("/regresult failed");
                            }
                        }
                    }
                    //work cycle
                    while (true) {
                        socket.setSoTimeout(0);
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                out.writeUTF("/end");
                                break;
                            }

                            if (str.startsWith("/w ")) {
                                String[] token = str.split("\\s", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                server.sendPrivateMessage(this, token[1], token[2]);
                            }

                            if (str.startsWith("/chnick ")) {
                                String[] token = str.split("\\s", 2);
                                if (token.length < 2) {
                                    continue;
                                }
                                server.changeNickname(this.login, token[1]);
                            }

                        } else {
                            server.broadcastMessage(this, str);
                        }

                    }
                } catch (SocketTimeoutException e) {
                    logger.log(Level.INFO, "Timeout, connection terminated!");
                    sendMessage("Timeout, connection terminated!");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    sendMessage("/timeout");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    logger.log(Level.INFO, "Client offline");
                    server.unsubscribe(this);
                    try {
                        socket.close();
                        executorService.shutdown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return server.getAuthService().getNicknameByLoginAndPassword(login, password);
    }


    void sendMessage(String str) {
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLogin() {
        return login;
    }
}
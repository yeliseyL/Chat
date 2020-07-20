package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

public class ClientHandler {
    Server server;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    private String nickname;
    private String login;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //auth cycle
                    while (true) {
                        socket.setSoTimeout(120_000);
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

                            if (newNick != null) {
                                if (!server.isLoginAuthorized(login)) {
                                    sendMessage("/authok " + newNick);
                                    nickname = newNick;
                                    server.subscribe(this);
                                    System.out.printf("Client %s connected %n", nickname);
                                    break;
                                } else {
                                    sendMessage("This login has been already authorized.");
                                }
                            } else {
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

                        } else {
                            server.broadcastMessage(this, str);
                        }

                    }
                } catch (SocketTimeoutException e) {
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
                    System.out.println("Client offline");
                    server.unsubscribe(this);
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

    public String getNickname() {
        return nickname;
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

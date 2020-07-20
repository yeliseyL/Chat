package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {
    private List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        clients = new Vector<>();
        authService = new DatabaseAuthService();

        final int PORT = 8806;
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Server is running");

            while (true) {
                socket = server.accept();
                System.out.println("Client connected");
                System.out.println("Remote socket address: " + socket.getRemoteSocketAddress());
                System.out.println("Local socket address: " + socket.getLocalSocketAddress());
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void broadcastMessage(ClientHandler sender, String str) {
        String message = String.format("%s: %s", sender.getNickname(), str);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    void sendPrivateMessage(ClientHandler sender, String receiver, String str) {
        String message = String.format("[%s] to [%s]: %s", sender.getNickname(), receiver, str);
        for (ClientHandler client : clients) {
            if (client.getNickname().equals(receiver)) {
                client.sendMessage(message);
                sender.sendMessage(message);
                return;
            }
        }
        sender.sendMessage(String.format("Client %s not found.%n", receiver));
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public boolean isLoginAuthorized(String login) {
        for (ClientHandler client : clients) {
            if (client.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clientlist ");
        for (ClientHandler client : clients) {
            sb.append(client.getNickname()).append(" ");
        }

        String message = sb.toString();

        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
}


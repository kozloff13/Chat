package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * сервер
 */

public class Server implements IConnect {

    public static void main(String[] args) {
        new Server();
    }

    //соединения складываются в ArrayList
    private final ArrayList<Connect> connections = new ArrayList<>();

    //метод с серверным сокетом
    private Server() {
        System.out.println("Server running/Сервер запущен");
        try (ServerSocket serverSocket = new ServerSocket(8189)){
            while (true) {
                try {
                    new Connect(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("server.Connect exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendToChat(String value) {
        System.out.println(value);
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++) {
            connections.get(i).sendString(value);
        }
    }

    public void conReady(Connect connect) {
        connections.add(connect);
        sendToChat("Подключился клиент: " + connect);
    }

    public void receiveString(Connect connect, String value) {
        sendToChat(value);
    }

    public void onDisconnect(Connect connect) {
        connections.remove(connect);
        sendToChat("Отключился клиент: " + connect);
    }

    public void except(Connect connect, Exception e) {
        System.out.println("server.Connect exception" + e);
    }
}

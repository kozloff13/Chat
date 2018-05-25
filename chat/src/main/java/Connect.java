import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connect {

    private final Socket socket;
    private final Thread thread;
    private final IConnect eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public Connect(IConnect eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port));
    }

    public Connect(final IConnect eventListener, Socket socket) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    eventListener.conReady(Connect.this);
                    while (!thread.isInterrupted()) {
                        eventListener.receiveString(Connect.this, in.readLine());
                    }
                } catch (IOException e) {
                    eventListener.except(Connect.this, e);
                } finally {
                    eventListener.onDisconnect(Connect.this);
                }
            }
        });
        thread.start();
    }

    public synchronized void sendString(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.except(this, e);
            disconnect();
        }
    }

    private synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.except(this, e);
        }
    }

    @Override
    public String toString() {
        return "Address: " + socket.getInetAddress() + "    Port: " + socket.getPort();
    }
}

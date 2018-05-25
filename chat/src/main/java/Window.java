import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * мой вариант простого мессенджера на основе материалов из интернетов
 * V 0.8 (без документации и комментариев)
 */

public class Window extends JFrame implements ActionListener, IConnect {

    private static final String ADDR = "localhost";
    private static int PORT = 8189;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final JTextArea log = new JTextArea();
    private final JTextField nicknameField = new JTextField("юзернейм"); //потом сюда надо вставить ник пользователя отдельной функцией
    private final JTextField messageField = new JTextField();

    private Connect connection;

    private Window() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        messageField.addActionListener(this);
        add(nicknameField, BorderLayout.NORTH);
        add(messageField, BorderLayout.SOUTH);

        setVisible(true);

        try {
            connection = new Connect(this, ADDR, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }

    }

    private synchronized void printMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Window();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = messageField.getText();
        if (message.equals("")) {
            return;
        }
        messageField.setText(null);
        connection.sendString(nicknameField.getText() + ": " + message);
    }

    @Override
    public void conReady(Connect connect) {
        printMessage("Подключение...");
    }

    @Override
    public void receiveString(Connect connect, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(Connect connect) {
        printMessage("Отключение");
    }

    @Override
    public void except(Connect connect, Exception e) {
        printMessage("Connection exception: " + e);
    }
}

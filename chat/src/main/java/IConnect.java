public interface IConnect {

    void conReady(Connect connect);
    void receiveString(Connect connect, String value);
    void onDisconnect(Connect connect);
    void except(Connect connect, Exception e);

}

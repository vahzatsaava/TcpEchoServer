
public class MultiThreadedEchoServer {

    public static void main(String[] args) {
        TcpServer tcpServer = new TcpServer();
        TpcClient client = new TpcClient();
        int port = 7;
        new Thread(() -> tcpServer.startServer(port)).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.startMultipleClients(port);

    }







}

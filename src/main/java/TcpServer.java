
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TcpServer {
    private final ExecutorService clientPool = Executors.newFixedThreadPool(10);

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientPool.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            log.error("Server exception: {}", e.getMessage(), e);
        } finally {
            clientPool.shutdown();
        }
    }

    private void handleClient(Socket clientSocket) {
        String clientAddress = clientSocket.getInetAddress().toString();
        log.info("START: Handling client [{}] in thread: {}", clientAddress, Thread.currentThread().getName());

        try (
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String received = new String(buffer, 0, bytesRead).trim();
                log.info("[{}] Received: {}", clientAddress, received);

                String response = received + "\n";
                outputStream.write(response.getBytes());
                outputStream.flush();
                log.info("[{}] Sent: {}", clientAddress, response.trim());
            }
        } catch (IOException e) {
            log.error("[{}] Client error: {}", clientAddress, e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error("[{}] Error closing connection: {}", clientAddress, e.getMessage());
            }
            log.info("END: Client [{}] disconnected", clientAddress);
        }
    }

}

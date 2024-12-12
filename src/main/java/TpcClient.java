import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
@Slf4j
public class TpcClient {
    public void startMultipleClients(int port) {
        Runnable clientTask = () -> {
            String clientName = Thread.currentThread().getName();

            try (Socket socket = new Socket("localhost", port);
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                log.info("{}: Connected to Echo Server", clientName);

                for (int i = 1; i <= 5; i++) {
                    String message = "Message from " + clientName + " #" + i;
                    log.info("{}: Sending: {}", clientName, message);
                    output.println(message);

                    String response = input.readLine();
                    log.info("{}: Received: {}", clientName, response);
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                log.error("{}: Client error: {}", clientName, e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                log.info("END: {} disconnected", clientName);
            }
        };

        for (int i = 0; i < 3; i++) {
            new Thread(clientTask, "Client-" + (i + 1)).start();
        }
    }
}

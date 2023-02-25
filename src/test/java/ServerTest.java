import com.example.MainServer.Server;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class ServerTest {
    private static final Boolean EXPECTED = true;

    @DisplayName("JUnit test for correct run")
    @Test
    public void testServerStart() throws Exception {
        Server server = new Server();

        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        Thread.sleep(1000);

         assertEquals(EXPECTED,serverThread.isAlive());

        serverThread.interrupt();
    }
}

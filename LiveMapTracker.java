import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.awt.Desktop;

public class LiveMapTracker {

    // Advanced State management using Enums
    enum ServerMode { STANDBY, CAPTURING, MAP_LAUNCHED }
    private static ServerMode currentMode = ServerMode.STANDBY;

    // Tracker variable to avoid spamming multiple browser tabs every 5 seconds
    private static String lastProcessedStamp = "";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/locate", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    currentMode = ServerMode.CAPTURING;
                    
                    InputStream is = exchange.getRequestBody();
                    String body = new String(is.readAllBytes());
                    
                    // Parse parameters: lat, lng, name, matric
                    String[] data = body.split(",");
                    String lat = data[0];
                    String lng = data[1];
                    String studentName = data[2];
                    String matricNo = data[3];

                    System.out.println("\n[ENGINE STATUS: " + currentMode + "]");
                    System.out.println("Incoming Node Sync -> " + studentName + " (" + matricNo + ")");
                    System.out.println("Coordinates: Latitude " + lat + " | Longitude " + lng);

                    // To prevent tab explosion: only launch the browser tab if the location coordinate values actually changed
                    String currentStamp = lat + "," + lng;
                    if (!currentStamp.equals(lastProcessedStamp)) {
                        lastProcessedStamp = currentStamp;
                        currentMode = ServerMode.MAP_LAUNCHED;

                        // Native directional search query layout string for Google Maps
                        String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng;

                        if (Desktop.isDesktopSupported()) {
                            try {
                                System.out.println(">>> Action: Launching isolated map window tracking node...");
                                Desktop.getDesktop().browse(new URI(googleMapsUrl));
                            } catch (Exception e) {
                                System.out.println("Window interface context error: " + e.getMessage());
                            }
                        }
                    } else {
                        System.out.println(">>> Action: Coordinates identical to last stream loop. Suppressing tab window spam to save memory.");
                    }

                    String response = "Java Stream Server Synced State Safely.";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    
                    currentMode = ServerMode.STANDBY;
                }
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("=========================================================");
        System.out.println("  Java Runtime Logic & Streams Server Started On Port 8080 ");
        System.out.println("=========================================================");
    }
}
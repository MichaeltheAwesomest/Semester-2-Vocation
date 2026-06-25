package Location;

	 import com.sun.net.httpserver.HttpServer;
	 import com.sun.net.httpserver.HttpHandler;
	 import com.sun.net.httpserver.HttpExchange;
	 import java.io.IOException;
	 import java.io.InputStream;
	 import java.io.OutputStream;
	 import java.net.InetSocketAddress;

	 public class LiveMapTracker {

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
	                     InputStream is = exchange.getRequestBody();
	                     String body = new String(is.readAllBytes());
	                     
	         
	                     String[] data = body.split(",");
	                     String lat = data[0];
	                     String lng = data[1];
	                     String name = data[2];
	                     String matric = data[3];

	                     System.out.println("[Server Foundational Branch] Packet read from: " + name);

	                     String response = "Base Packet Processed";
	                     exchange.sendResponseHeaders(200, response.length());
	                     OutputStream os = exchange.getResponseBody();
	                     os.write(response.getBytes());
	                     os.close();
	                 }
	             }
	         });

	         server.setExecutor(null);
	         server.start();
	         System.out.println("Base Network Server active on port 8080...");
	     }
	 }


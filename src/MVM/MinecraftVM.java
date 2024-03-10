import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MinecraftVM {

    public static void executeCommand(String command) {
        if (command.startsWith("/")) {
            String[] parts = command.split(" ", 2);
            String cmd = parts[0].substring(1);

            switch (cmd) {
                case "say":
                    return "Saying: " + message;
                    break;
                case "title":
                    return "Title command: " + titleCommand;
                    break;
                case "give":
                    return "Give command: " + giveCommand;
                    break;
                case "fill":
                    return "Fill command: " + fillCommand;
                    break;
                default:
                    return "Unsupported command: " + cmd;
            }
        } else {
            return "Invalid command format. Command must start with '/'";
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(2303);
            while (true) {
                Socket client = serverSocket.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String line;
                String request = "";
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    request += line + "\n";
                }
                String[] requestLines = request.split("\n");
                String getRequestLine = requestLines[0];
                String[] requestParts = getRequestLine.split(" ");
                String requestMethod = requestParts[0];
                String requestPath = requestParts[1];
                Map<String, String> queryParams = new HashMap<>();
                if (requestPath.contains("?")) {
                    String queryString = requestPath.substring(requestPath.indexOf("?") + 1);
                    String[] params = queryString.split("&");
                    for (String param : params) {
                        String[] parts = param.split("=");
                        queryParams.put(parts[0], parts[1]);
                    }
                }
                String command = queryParams.get("command");
                String result = "";
                if (command != null) {
                    result = executeCommand(command);
                }

                OutputStream clientOutStream = client.getOutputStream();
                clientOutStream.write(
                        ("HTTP/1.1 200 OK\n"
                                + "Content-Type: text/html\n"
                                + "\n"
                                + result).getBytes()
                );
                clientOutStream.flush();
                clientOutStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


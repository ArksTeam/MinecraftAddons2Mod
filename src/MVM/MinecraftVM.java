import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftCommandExecutor {

    public static void executeCommand(String command) {
        if (command.startsWith("/")) {
            String[] parts = command.split(" ", 2);
            String cmd = parts[0].substring(1);

            switch (cmd) {
                case "say":
                    handleSayCommand(parts[1]);
                    break;
                case "title":
                    handleTitleCommand(parts[1]);
                    break;
                case "give":
                    handleGiveCommand(parts[1]);
                    break;
                case "fill":
                    handleFillCommand(parts[1]);
                    break;
                default:
                    System.out.println("Unsupported command: " + cmd);
            }
        } else {
            System.out.println("Invalid command format. Command must start with '/'");
        }
    }

    private static void handleSayCommand(String message) {
    }

    private static void handleTitleCommand(String titleCommand) {
    }

    private static void handleGiveCommand(String giveCommand) {
    }

    private static void handleFillCommand(String fillCommand) {
        
    }
}

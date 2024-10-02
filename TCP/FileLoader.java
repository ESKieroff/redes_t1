import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class FileLoader {
    public static void loadData(Map<String, User> users, Map<String, List<String>> followers,
            Map<String, List<String>> newsletters, Map<String, List<String>> newsletterMessages) throws IOException {
        Path usersPath = Paths.get("data/users.txt");
        Path followersPath = Paths.get("data/followers.txt");
        Path newslettersPath = Paths.get("data/newsletters.txt");
        Path newsletterMessagesPath = Paths.get("data/newsletterMessages.txt");

        if (Files.exists(usersPath)) {
            List<String> lines = Files.readAllLines(usersPath);
            for (String line : lines) {
                String[] parts = line.split(" ");
                users.put(parts[0], new User(parts[0], parts[1]));
            }
        }

        if (Files.exists(followersPath)) {
            List<String> lines = Files.readAllLines(followersPath);
            for (String line : lines) {
                String[] parts = line.split(" ");
                followers.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
            }
        }

        if (Files.exists(newslettersPath)) {
            List<String> lines = Files.readAllLines(newslettersPath);
            for (String line : lines) {
                String[] parts = line.split(" ");
                newsletters.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
            }
        }

        if (Files.exists(newsletterMessagesPath)) {
            List<String> lines = Files.readAllLines(newsletterMessagesPath);
            for (String line : lines) {
                String[] parts = line.split(" ", 2);
                newsletterMessages.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
            }
        }
    }
}

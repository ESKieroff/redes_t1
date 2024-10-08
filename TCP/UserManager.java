import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserManager {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, List<String>> followers = new ConcurrentHashMap<>();
    private final Set<String> onlineUsers = new HashSet<>();
    private final Map<String, List<String>> newsletters = new HashMap<>();
    private final Map<String, List<String>> newsletterMessages = new HashMap<>();

    // register
    public synchronized void registerUser(String username, String password) {
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Erro: Usuário já registrado.");
        } else {
            users.put(username, new User(username, password));
            saveUsersToFile();
            System.out.println("Usuário " + username + " registrado com sucesso.");
        }
    }

    private void saveUsersToFile() {
        try {
            PrintWriter writer = new PrintWriter("data/users.txt", "UTF-8");
            for (User user : users.values()) {
                writer.println(user.getUsername() + " " + user.getPassword());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    // login
    public synchronized boolean loginUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            onlineUsers.add(username);
            return true;
        }
        return false;
    }

    public synchronized void logoutUser(String username) {
        onlineUsers.remove(username);
    }

    // online
    public void addOnlineUser(String username) {
        onlineUsers.add(username);
    }

    public void removeOnlineUser(String username) {
        onlineUsers.remove(username);
    }

    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(onlineUsers);
    }

    // isUserOnline
    public boolean isUserOnline(String username) {
        return onlineUsers.contains(username);
    }

    // follow
    public void addFollower(String follower, String followee) {
        followers.computeIfAbsent(follower, k -> new CopyOnWriteArrayList<>()).add(followee);
    }

    public void removeFollower(String follower, String followee) {
        List<String> userFollowers = followers.getOrDefault(follower, new CopyOnWriteArrayList<>());
        userFollowers.remove(followee);
    }

    public void notifyFollowers(String userName, String message) {
        List<String> userFollowers = followers.getOrDefault(userName, Collections.emptyList());
        for (String follower : userFollowers) {
            User followerUser = users.get(follower);
            if (followerUser != null && onlineUsers.contains(follower)) {
                followerUser.getWriter().println(message);
            }
        }
    }

    public List<String> getFollowers(String userName) {
        return followers.getOrDefault(userName, Collections.emptyList());
    }

    // newsletter
    public boolean createNewsletter(String username) {
        return newsletters.putIfAbsent(username, new CopyOnWriteArrayList<>()) == null;
    }

    public boolean deleteNewsletter(String username) {
        return newsletters.remove(username) != null && newsletterMessages.remove(username) != null;
    }

    public boolean subscribeToNewsletter(String subscriber, String newsletterOwner) {
        List<String> subscribers = newsletters.computeIfAbsent(newsletterOwner, k -> new CopyOnWriteArrayList<>());
        return subscribers.add(subscriber);
    }

    public boolean unsubscribeFromNewsletter(String subscriber, String newsletterOwner) {
        List<String> subscribers = newsletters.get(newsletterOwner);
        return subscribers != null && subscribers.remove(subscriber);
    }

    public List<String> getSubscribedNewsletters(String username) {
        List<String> subscribedNewsletters = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : newsletters.entrySet()) {
            if (entry.getValue().contains(username)) {
                subscribedNewsletters.add(entry.getKey());
            }
        }
        return subscribedNewsletters;
    }

    public boolean sendNewsletterMessage(String sender, String message) {
        List<String> subscribers = newsletters.get(sender);
        if (subscribers != null && !subscribers.isEmpty()) {
            for (String subscriber : subscribers) {
                User subscriberUser = getUser(subscriber);
                if (subscriberUser != null && subscriberUser.getWriter() != null) {
                    subscriberUser.getWriter().println("Newsletter de " + sender + ": " + message);
                }
            }
            return true;
        }
        return false;
    }

}

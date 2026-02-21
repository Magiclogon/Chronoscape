package ma.ac.emi.UI;

import java.util.*;


public class NavigationManager {
    
    private final Deque<String> backStack;
    private final Deque<String> forwardStack;
    private String currentScreen;
    private final Set<String> nonNavigableScreens;
    private final List<NavigationListener> listeners;
    
    public NavigationManager() {
        this.backStack = new ArrayDeque<>();
        this.forwardStack = new ArrayDeque<>();
        this.currentScreen = null;
        this.nonNavigableScreens = new HashSet<>();
        this.listeners = new ArrayList<>();
        
        addNonNavigableScreen("LOADING");
        addNonNavigableScreen("GAME"); 
    }
    

    public void navigateTo(String screenName) {
        if (screenName == null || screenName.equals(currentScreen)) {
            return; 
        }
        
        if (currentScreen != null && !nonNavigableScreens.contains(currentScreen)) {
            backStack.push(currentScreen);
        }
        
        forwardStack.clear();
        
        String previousScreen = currentScreen;
        currentScreen = screenName;
        
        notifyNavigation(previousScreen, currentScreen);
    }
    

    public String goBack() {
        if (!canGoBack()) {
            return null;
        }
        
        String previous = backStack.pop();
        
        if (currentScreen != null && !nonNavigableScreens.contains(currentScreen)) {
            forwardStack.push(currentScreen);
        }
        
        String previousScreen = currentScreen;
        currentScreen = previous;
        
        notifyNavigation(previousScreen, currentScreen);
        
        return currentScreen;
    }
    

    public String goForward() {
        if (!canGoForward()) {
            return null;
        }
        
        String next = forwardStack.pop();
        
        if (currentScreen != null && !nonNavigableScreens.contains(currentScreen)) {
            backStack.push(currentScreen);
        }
        
        String previousScreen = currentScreen;
        currentScreen = next;
        
        notifyNavigation(previousScreen, currentScreen);
        
        return currentScreen;
    }
    
  
    public void jumpTo(String screenName) {
        if (screenName == null || screenName.equals(currentScreen)) {
            return;
        }
        
        String previousScreen = currentScreen;
        currentScreen = screenName;
        
        // Notify listeners
        notifyNavigation(previousScreen, currentScreen);
    }
    

    public String backToRoot(String rootScreen) {
        backStack.clear();
        forwardStack.clear();
        
        String previousScreen = currentScreen;
        currentScreen = rootScreen;
        
        // Notify listeners
        notifyNavigation(previousScreen, currentScreen);
        
        return currentScreen;
    }
    
    
    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }
    
  
    public String getCurrentScreen() {
        return currentScreen;
    }
    
 
    public String peekBack() {
        return backStack.isEmpty() ? null : backStack.peek();
    }
    
   
    public String peekForward() {
        return forwardStack.isEmpty() ? null : forwardStack.peek();
    }

    public List<String> getBackHistory() {
        return new ArrayList<>(backStack);
    }
    
    
    public List<String> getForwardHistory() {
        return new ArrayList<>(forwardStack);
    }
    
    
    public void clearHistory() {
        backStack.clear();
        forwardStack.clear();
    }
    
    
    public void addNonNavigableScreen(String screenName) {
        nonNavigableScreens.add(screenName);
    }
    
   
    public void removeNonNavigableScreen(String screenName) {
        nonNavigableScreens.remove(screenName);
    }
    
    
    public void addNavigationListener(NavigationListener listener) {
        listeners.add(listener);
    }
    
    
    public void removeNavigationListener(NavigationListener listener) {
        listeners.remove(listener);
    }
    
    
    private void notifyNavigation(String from, String to) {
        for (NavigationListener listener : listeners) {
            listener.onNavigate(from, to);
        }
    }
    
    
    public String getNavigationState() {
        StringBuilder sb = new StringBuilder();
        sb.append("Navigation State:\n");
        sb.append("  Current: ").append(currentScreen).append("\n");
        sb.append("  Back Stack: ").append(backStack).append("\n");
        sb.append("  Forward Stack: ").append(forwardStack).append("\n");
        sb.append("  Can Go Back: ").append(canGoBack()).append("\n");
        sb.append("  Can Go Forward: ").append(canGoForward()).append("\n");
        return sb.toString();
    }
    
  
    public interface NavigationListener {
        
        void onNavigate(String from, String to);
    }
}

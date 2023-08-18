package businesslogic.user;

public class UserManager {
    private User currentUser;

    public void fakeLogin(String username) //TODO: bisogna implementare il login vero!
    {
        this.currentUser = User.loadUser(username);
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }
}

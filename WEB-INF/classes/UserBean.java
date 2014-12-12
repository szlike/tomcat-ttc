public class UserBean {

    /** DB User table id for this username */
    private int id;

    /** the username */
    private String username;

    /** the password */
    private String password;

    /** the first_name */
    private String first_name;

    /** the last_name */
    private String last_name;

    /** the email */
    private String email;

    /** the phone */
    private String phone;

    /** whether the user is logged in or not */
    private boolean loggedIn;

    /**
     * Get the user id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Set the user id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the user name
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Set the user name
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Set the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the first name
     */
    public String getFirstname() {
        return this.first_name;
    }

    /**
     * Set the first name
     */
    public void setFirstname(String first_name) {
        this.first_name = first_name;
    }

    /**
     * Get the last name
     */
    public String getLastname() {
        return this.last_name;
    }

    /**
     * Set the last name
     */
    public void setLastname(String last_name) {
        this.last_name = last_name;
    }

    /**
     * Get the email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Set the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the phone
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     * Set the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Set whether we are currently logged in or not
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    /**
     * Determine if we are currently logged in or not
     */
    public boolean getLoggedIn() {
        return this.loggedIn;
    }
}

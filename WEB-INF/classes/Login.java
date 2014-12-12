import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class Login extends HttpServlet {
  //ServletContext context = getServletContext();
  //synchronized(context){
  //  context.setAttribute("demo", new UserBean);
  //}

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
      ServletException, IOException {
    process(request, response);
  }

  protected void doPost(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException,
      IOException {
    process(request, response);
  }

  /** 
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void process(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                         IOException {
    synchronized(this) {

       int status = 0; // holds status of login

    	String nextPage = request.getParameter("page");
    	String authenticated = "true";
    	int userId = 0;

    	HttpSession session = request.getSession(true);
    	// session.setMaxInactiveInterval(60);  // now set by init() in Controller
    	UserBean user = (UserBean) request.getAttribute(PublicConstants.USERBEAN_ATTR);

    	// if the userbean doesn't exists, create it
    	if (user == null) {
            user = new UserBean();
      	     session.setAttribute(PublicConstants.USERBEAN_ATTR, user);
    	}

    	if (!user.getLoggedIn()) {
      	    // retrieve request parameters; if we used a JSP on the front-end
	    // would already have these values in a bean as part of request
      	    String username = request.getParameter(PublicConstants.USERNAME_PARAM);
           String password = request.getParameter(PublicConstants.PASSWORD_PARAM);

      	    // record the username and password values in a User Bean
           user.setUsername(username);
      	    user.setPassword(password);
      	    // in case malicious user posts these values
      	    user.setLoggedIn(false);
      	    user.setId(0);

      	    // attempt to login
      	    authenticate(user);

      	    // if we failed, redirect to the login page
      	    if (user.getLoggedIn()) {
		// since user is now logged in, we will retrieve their Stoplist
            	HashMap<String, StopBean> stoplist = this.getstoplist(user);
		session.setAttribute("stoplist", stoplist);
	    }
      	    else {
              // username can be found in the database and password is not correct
        	nextPage = "#loginPage";
              authenticated = "Password not valid";
      	    }
        } 
    // debugging
    //System.out.println(nextPage + "," + authenticated);

    // redirect to the next action, if login invoked on startup of another action
    /* in server-controller model without Ajax, tell client which page to load ...
       response.sendRedirect(nextPage);
    */
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    out.println("{\"authenticated\":\"" + authenticated
	+ "\", \"page\":\"" + nextPage + "\", \"userId\":\"" + user.getId() + "\"}");
     }
    }

     /*
     * Set the loggedIn and userId property of the userbean if authentication succeeds.
     * If username can not be found, then set userId to -1. If username can be found but
     * password can not be verified, then set userId to 0. Otherwise, set user to the
     * corresponding username and loggedIn and userId.
     */
    private void authenticate(UserBean user){

        Connection con;
        // using prepared statement for security
        // DEPRECATED lowercase the username when comparing, username is not casesensitive
        //String searchQuery = "SELECT * FROM Users WHERE LOWER(username) = ? AND password = ?";
        String searchQuery = "SELECT * FROM Users WHERE username = ? AND password = ?";

        // connect to db and retrieve routes
        try {
            // get connection pool
            DataSource dbcp = (DataSource)getServletContext().getAttribute("dbpool");
            con = dbcp.getConnection();

            PreparedStatement searchQueryP = con.prepareStatement(searchQuery);

            // set username and password
            //searchQueryP.setString(1, user.getUsername().toLowerCase());
            searchQueryP.setString(1, user.getUsername());
            searchQueryP.setString(2, user.getPassword());

            ResultSet rs = searchQueryP.executeQuery();

            if (rs.next()) { // if a row is returned, they are authenticated
                // fill the userBean with the rest of the properties
                user.setId(rs.getInt("id"));
                user.setLoggedIn(true);
            }
            else {
                 searchQuery = "SELECT * FROM Users WHERE username = ?";
                 searchQueryP = con.prepareStatement(searchQuery);
                 searchQueryP.setString(1, user.getUsername());
                 rs = searchQueryP.executeQuery();
                 if (rs.next()) {
                      user.setId(0);
                 }
                 else {
                      user.setId(-1);
                 }                     
            }
            // close result set
            rs.close();
            // close prep statement
            searchQueryP.close();
            //close db connection
            con.close();
        }
        catch(SQLException ex) {
          System.err.println("SQLException: " + ex.getMessage());
        }
        catch (Exception e) {
          e.printStackTrace();
        }
    }

    /*
     * returns a HashMap of user's stoplist
     */
    private HashMap<String, StopBean> getstoplist(UserBean user){

        // initialize a new stopmap
        HashMap<String, StopBean> StopMap = new HashMap<String, StopBean>();

        Connection con;
        // use prepared statement
        String searchQuery = "SELECT * FROM MyStops WHERE user_id = ?";

        // connect to db and retrieve routes
        try {
            // get connection pool
            DataSource dbcp = (DataSource)getServletContext().getAttribute("dbpool");
            con = dbcp.getConnection();

            PreparedStatement searchQueryP = con.prepareStatement(searchQuery);

            searchQueryP.setInt(1, user.getId());
            ResultSet rs = searchQueryP.executeQuery();

            while(rs.next()){ // convert results to StopBeans
                StopBean stop = new StopBean();
                stop.setRouteid(rs.getString("routeid"));
                stop.setRoute_title(rs.getString("route_title"));
                stop.setRunid(rs.getString("runid"));
                stop.setRun_title(rs.getString("run_title"));
                stop.setStopid(rs.getString("stopid"));
                stop.setStop_title(rs.getString("stop_title"));
                stop.setUserId(user.getId());                
                StopMap.put(stop.getStopid(), stop); //add to hashmap keyed by Stop id
            }
            // close result set
            rs.close();
            // close prep statement
            searchQueryP.close();
            //close db connection
            con.close();
        }
        catch(SQLException ex) {
          System.err.println("SQLException: " + ex.getMessage());
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        return StopMap;
    }
}

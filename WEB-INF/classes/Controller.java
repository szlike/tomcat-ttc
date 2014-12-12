import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.*;

/*
 * The Front Controller, handles all requests
 */
public class Controller extends HttpServlet {

    private static final String DB_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_USER = "cscc09f12_10";
    private static final String DB_PWD = "password";

     /*
     * Initialize database once at startup 
     */
    public void init() {
        Connection con; // to store the connection object
        int rowsEffected = 0;   // intended for error checking; not used here

        try {
                // initialize connection pool
                DriverAdapterCPDS ds = new DriverAdapterCPDS();
                ds.setDriver(DB_JDBC_DRIVER); // set the DB driver
                // ds.setUrl("jdbc:mysql:" + getServletContext().getInitParameter("dbUrl")); // set the url from dbUrl // FOR H2 DB
                ds.setUrl("jdbc:mysql:" + "//mathlab.utsc.utoronto.ca:3306/cscc09f12_10");
                ds.setUser(DB_USER);
                ds.setPassword(DB_PWD);

                SharedPoolDataSource dbcp = new SharedPoolDataSource();
                dbcp.setConnectionPoolDataSource(ds); // set the datasource for connection pool
		
		ServletContext context = getServletContext();
		synchronized(context){		
                	context.setAttribute("dbpool",dbcp); // make it available to all servlets
		}
        }
        catch (Exception ex) {
                getServletContext().log("SQLGatewayPool Error: " + ex.getMessage());
        }
        try {
          String dbInit = getServletContext().getInitParameter("dbInit");
          if (dbInit.equals("true")) {	// dbInit context says to initialize DB
            // get connection pool
            DataSource dbcp = (DataSource)getServletContext().getAttribute("dbpool");
            con = dbcp.getConnection();

            // Get a statement (used to issue SQL statements to the DB)
            Statement stmt = con.createStatement();

            // create Users table and drop existing
            String query = "DROP TABLE IF EXISTS Users";
            rowsEffected = stmt.executeUpdate(query);

            //query = "CREATE CACHED TABLE Users ("
            query = "CREATE TABLE Users ("
                    + "id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "username varchar(16) NOT NULL,"
                    + "password varchar(16) NOT NULL,"
                    + "first_name varchar(64) NOT NULL,"
                    + "last_name varchar(64) NOT NULL,"
                    + "email varchar(255) DEFAULT NULL,"
                    + "phone varchar(64) DEFAULT NULL"
		      + ");";

            rowsEffected = stmt.executeUpdate(query);
            //System.out.println("DEBUG: " + rowsEffected);

            // insert sample user
            query = "INSERT INTO Users (username, password, first_name, last_name, email, phone) VALUES"
                    + "('testing', '4Testing', 'Tom', 'Tester', 'tom@gmail.com', '1-888-555-1212')";

            rowsEffected = stmt.executeUpdate(query);

            // create MyStops table and drop existing
            query = "DROP TABLE IF EXISTS MyStops";
            rowsEffected = stmt.executeUpdate(query);

            //query = "CREATE CACHED TABLE MyStops ("
            query = "CREATE TABLE MyStops ("
		      + "id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "routeid varchar(3) NOT NULL,"
                    + "route_title varchar(255) NOT NULL,"
                    + "runid varchar(12) NOT NULL,"
                    + "run_title varchar(255) NOT NULL,"
                    + "stopid varchar(12) NOT NULL,"
                    + "stop_title varchar(255) NOT NULL,"
                    + "user_id bigint NOT NULL"
		      + ");";

            rowsEffected = stmt.executeUpdate(query);
            //System.out.println("DEBUG: Controller init MyStops Table: " + rowsEffected);

            // should check to make sure table was created without an error
            stmt.close();
            con.close();
          }
        }
        catch(SQLException ex) {
          System.err.println("SQLException: " + ex.getMessage());
        }
        catch (Exception e) {
          e.printStackTrace();
        }
  }

  /** 
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  private void processRequest(HttpServletRequest request, HttpServletResponse response) throws
      IOException, ServletException {

    // Gets the session
    HttpSession session = request.getSession(true);

    // Get the session idle-timeout interval from context param
    String timeoutstr = getServletContext().getInitParameter("idleTimeout");

    // Convert to int
    int timeout = Integer.parseInt(timeoutstr);

    // Set the session time out
    session.setMaxInactiveInterval(timeout);

    // get the action parameter
    String action = request.getParameter("action");

    System.out.println("DEBUG Controller: " + timeout + " : " + action);
    if (action != null){ // make sure action var is passed

    // dispatch the requested servlet
    // all requests are forwarded to appropriate servlet
    if (action.equals("login")) {
        RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("Login");
        dispatcher.forward(request, response);
        }
    else if (action.equals("register")) {
        RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("Register");
        dispatcher.forward(request, response);
    }
    else if (action.equals("session_check")) {
        RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("SessionCheck");
        dispatcher.forward(request, response);
    }
    else if (action.equals("stopslist")) {
        RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("MyStopslist");
        dispatcher.forward(request, response);
	}
    else if (action.equals("save")) {
        RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("SaveStop");
        dispatcher.forward(request, response);
	}
    else if (action.equals("delete")) {
        RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("DeleteStop");
        dispatcher.forward(request, response);
	}
    else if (action.equals("logout")){
        session.invalidate(); // invalidated the session
        // response.sendRedirect("/a2/index.html"); // http redirect to main page
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println("{\"authenticated\":\"" + false
	          + "\", \"page\":\"" + request.getParameter("page") + "\"}");
    }
    else {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/badrequest.jsp");
        dispatcher.forward(request, response);
     }
    } // action var not passed
    else {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/badrequest.jsp");
        dispatcher.forward(request, response);
    }
  }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing Servlet description
     */
    public String getServletInfo() {
        return "Controller Servlet for iTrans";
    }// </editor-fold>
}

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;


public class Register extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {

    synchronized(this) {
        UserBean user = new UserBean();

        String status = "false";
        String nextPage = request.getParameter("page");
        

        user.setUsername(request.getParameter(PublicConstants.USERNAME_PARAM));
        user.setPassword(request.getParameter(PublicConstants.PASSWORD_PARAM));
        user.setFirstname(request.getParameter(PublicConstants.FIRSTNAME_PARAM));
        user.setLastname(request.getParameter(PublicConstants.LASTNAME_PARAM));
        user.setEmail(request.getParameter(PublicConstants.EMAIL_PARAM));
        user.setPhone(request.getParameter(PublicConstants.PHONE_PARAM));
        user.setLoggedIn(false);
        user.setId(0);
        
        HttpSession session = request.getSession(true);
        UserBean userBean = (UserBean)session.getAttribute(PublicConstants.USERBEAN_ATTR);

        

        if ( !checkuser(user) ) {
             if ( register(user) > 0 ) {
                  status = "true";
                  session.setAttribute(PublicConstants.USERBEAN_ATTR, null);
             }
             else {
                  status = "something wrong!";
             }
        }
        else {
             status = user.getUsername().toLowerCase() + " is already taken."
                      + " Please try another username!";
        }

        response.setContentType("application/json");
        PrintWriter o = response.getWriter();
        o.println("{\"status\":\"" + status + "\", \"page\":\"" + nextPage + "\"}");
     }
    }

     private int register (UserBean user){

        Connection connect;
        int rowsef;
        rowsef= 0;

        String search = "INSERT INTO Users (username, password, first_name, last_name, email, phone) VALUES"
                + "(?, ?, ?, ?, ?, ?)";

        try {
          
            DataSource dbcp = (DataSource)getServletContext().getAttribute("dbpool");
            connect = dbcp.getConnection();

            PreparedStatement search1= connect.prepareStatement(search);

            search1.setString(1, user.getUsername());
            search1.setString(2, user.getPassword());
            search1.setString(3, user.getFirstname());
            search1.setString(4, user.getLastname());
            search1.setString(5, user.getEmail());
            search1.setString(6, user.getPhone());

            rowsef = search1.executeUpdate();

            search1.close();

            connect.close();
        }
        catch(SQLException ex) {
          System.err.println("SQLException: " + ex.getMessage());
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        return rowsef;
    }

    private boolean checkuser (UserBean user){

        Connection connect;
        String search = "SELECT * FROM Users WHERE username = ?";

        boolean found = false;

        try {
            DataSource dbcp = (DataSource)getServletContext().getAttribute("dbpool");
            connect = dbcp.getConnection();

            PreparedStatement search1 = connect.prepareStatement(search);

            search1.setString(1, user.getUsername());

            ResultSet rs = search1.executeQuery();

            if(rs.next()){ 
                found = true;
            }

            rs.close();

            search1.close();

            connect.close();
        }
        catch(SQLException ex) {
          System.err.println("SQLException: " + ex.getMessage());
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        return found;
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    public String getServletInfo() {
        return "Save Stop to MyStops table under User Id";
    }
}

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

/*
 * SaveStop servlet - handles Stop saving under user id
 */
public class SaveStop extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {

	 String nextPage = request.getParameter("page");
        int status;
        status = 0;
        // new Stop
        StopBean stop = new StopBean();
        // populate from the request params
        stop.setRouteid(request.getParameter("routeid"));
        stop.setRoute_title(request.getParameter("route_title"));
        stop.setRunid(request.getParameter("runid"));
        stop.setRun_title(request.getParameter("run_title"));
        stop.setStopid(request.getParameter("stopid"));
        stop.setStop_title(request.getParameter("stop_title"));
        stop.setUserId(Integer.parseInt(request.getParameter("user_id")));

        System.out.println(stop.getUserId()); // debug

        HttpSession session = request.getSession(true);

        // attempt to get UserBean
        UserBean user = (UserBean)session.getAttribute(PublicConstants.USERBEAN_ATTR);

        // attempt to get current list of stoplist (saved) Stops if they exist
        HashMap<String, StopBean> StopMap = (HashMap<String, StopBean>)session.getAttribute("stoplist");

	// if user session exists and stoplist StopMap exists
        if (user != null && StopMap != null) {

	    if (!StopMap.containsKey(stop.getStopid())) { // don't add duplicates
            	 status = saveStop(stop); // save stop
                 StopMap.put(stop.getStopid(), stop); // add the stop to the StopMap
            	 System.out.println("status: " + status);
                 System.out.println("DEBUG: Stop added for User: " + stop.getUserId());
	    }
            else {
                 System.out.println("DEBUG: Stop previously added by User: " + stop.getUserId());
            }
        }
        else { // user not logged in
            if (StopMap != null && !StopMap.containsKey(stop.getStopid())){// if they already have a StopMap associated
                StopMap.put(stop.getStopid(), stop); // add the Stop to it
                System.out.println("DEBUG: Stop Added for Guest: " + stop.getStopid());
                status = 1;
            }
            else if (StopMap == null) { // set a new StopMap in the session
                HashMap<String, StopBean> StopMapNew = new HashMap<String, StopBean>();
                StopMapNew.put(stop.getStopid(), stop); // add the current Stop
                status = 1;
                session.setAttribute("stoplist", StopMapNew);
                System.out.println("DEBUG: New StopMap created for Stop: " + stop.getStopid());
            }
            else {
                System.out.println("DEBUG: StopMap previously added Stop: " + stop.getStopid());
            }
        }
        //request.setAttribute("status", status);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println("{\"status\":\"" + status + "\", \"page\":\"" + nextPage + "\"}");
    }  

    /*
     * Given a Stop, save it to the database
     */
    private int saveStop(StopBean stop){

        Connection con;
        int rowsEffected;
        rowsEffected = 0;

        String searchQuery = "INSERT INTO MyStops (routeid, route_title, runid, run_title, stopid, stop_title, user_id) VALUES"
                + "(?,?,?,?,?,?,?)";

        // connect to db and retrieve routes
        try {
            // get connection pool
            DataSource dbcp = (DataSource)getServletContext().getAttribute("dbpool");
            con = dbcp.getConnection();

            PreparedStatement searchQueryP = con.prepareStatement(searchQuery);

            // populate the prepared statement with the Stop properties
            searchQueryP.setString(1, stop.getRouteid());
            searchQueryP.setString(2, stop.getRoute_title());
            searchQueryP.setString(3, stop.getRunid());
            searchQueryP.setString(4, stop.getRun_title());
            searchQueryP.setString(5, stop.getStopid());
            searchQueryP.setString(6, stop.getStop_title());
            searchQueryP.setInt(7, stop.getUserId());

            rowsEffected = searchQueryP.executeUpdate();

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
        return rowsEffected;
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
     * @return a String containing servlet description
     */

    public String getServletInfo() {
        return "Save Stop to MyStops table under User Id";
    }// </editor-fold>
}

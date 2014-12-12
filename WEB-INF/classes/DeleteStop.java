import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

/*
 * Stop deletion Servlet
 */

public class DeleteStop extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        int status = 0;

        // get the tag to delete by
        String stopId = request.getParameter("id");

        HttpSession session = request.getSession(true);

        // attempt to get UserBean if it exists
        UserBean user = (UserBean)session.getAttribute(PublicConstants.USERBEAN_ATTR);

        System.out.println("in DeleteStop");
        // get the saved list of Stops if it exists
        HashMap<String, StopBean> StopMap = (HashMap<String, StopBean>)session.getAttribute("stoplist");

        if (user != null && StopMap.containsKey(stopId)){ // user is logged in, check if they have this stop-id saved
            StopBean stop = StopMap.remove(stopId); // remove from StopMap
            // delete the Stop from the db as well
            status = deleteStop(stop);
            //System.out.println("DEBUG: Deleted User Stop: " + stopId);
        }
        else if (user == null) { // not logged in
            if (StopMap != null){
                if(StopMap.remove(stopId) != null){ //attempt to remove stop
                    status = 1; // found a stop to delete
                    //System.out.println("DEBUG: Deleted Guest Stop: " + stopId);
                }
            }
            else { // in case session has expired and user clicks delete
                status = 1;
            }
        }        
        request.setAttribute("status", status);
    }

    /*
     * Given a StopBean, deletes the stop from the db
     * @param StopBean stop
     */
     private int deleteStop (StopBean stop){

        Connection con;
        int rowsEffected;
        rowsEffected = 0;

        // use a prepared statement for security purposes
        String searchQuery = "DELETE FROM MyStops WHERE user_id = ? AND stopid = ?";

        // connect to db and retrieve routes
        try {
            // get connection pool
            DataSource dbcp = (DataSource)getServletContext().getAttribute("dbpool");
            con = dbcp.getConnection();

            // prepare the statement
            PreparedStatement searchQueryP = con.prepareStatement(searchQuery);

            // set the user id and tag
            searchQueryP.setInt(1, stop.getUserId());
            searchQueryP.setString(2, stop.getStopid());
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
        return "Short description";
    }// </editor-fold>
}

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

/*
 *
 */
public class SessionCheck extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {

       synchronized(this) {
              int user_id = 0;
              int getuserid = Integer.parseInt(request.getParameter("userId"));
              HttpSession session = request.getSession(false);          
              if ( request.getRequestedSessionId().equals(session.getId()) && getuserid != 0 ) {
                   user_id = getuserid;
              }
              response.setContentType("application/json");
              PrintWriter out = response.getWriter();
              out.println("{\"user_id\":\"" + Integer.toString(user_id) + "\"}");
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
     * @return a String containing servlet description
     */

    public String getServletInfo() {
        return "Check status of given user by userId.";
    }// </editor-fold>
}

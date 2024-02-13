package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
@WebServlet("/attendance")
public class AttendanceServlet extends HttpServlet {
    private String navigationBar() {
        return "<style>" +
                "nav ul {" +
                "    padding: 0;" +
                "    overflow: hidden;" +
                "    background-color: #313;" +
                "}" +
                "nav ul li {" +
                "    float: left;" +
                "}" +
                "nav ul li a {" +
                "    display: block;" +
                "    color: white;" +
                "    text-align: center;" +
                "    padding: 15px 18px;" +
                "}" +
                "nav ul li a:hover {" +
                "    background-color: #111;" +
                "}" +
                "</style>" +
                "<nav>" +
                "<ul>" +
                "<li><a href='students'>Show Students</a></li>" +
                "<li><a href='courses'>Show Courses</a></li>" +
                "<li><a href='attendance'>Search Attendance</a></li>" +
                "</ul>" +
                "</nav>";
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(navigationBar());

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:1337/gritacademy", "readuser", "");

            // creates a statement to get a list of students from the database
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, fname, lname FROM students");

            out.println("<html><body>");
            out.println("<h2>Search for Student's Courses</h2>");
            out.println("<form action='attendance' method='POST'>");
            out.println("Student ID: <input type='text' name='studentId'>");
            out.println("<input type='submit' value='Search'>");
            out.println("</form>");

            // displays a list of students for reference
            out.println("<h3>Students</h3>");
            out.println("<ul>");
            while (rs.next()) {
                out.println("<li>ID: " + rs.getInt("id") + ", Name: " + rs.getString("fname") + " " + rs.getString("lname") + "</li>");
            }
            out.println("</ul>");
            out.println("</body></html>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // gets the student id from the form submission
        String studentId = request.getParameter("studentId");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:1337/gritacademy", "readuser", "");

            // prepares a statement to get courses attended by the chosen student
            PreparedStatement stmt = con.prepareStatement("SELECT c.name FROM courses c JOIN attendance a ON c.id = a.`courses.id` JOIN students s ON s.id = a.`student.id` WHERE s.id = ?");
            stmt.setInt(1, Integer.parseInt(studentId));
            ResultSet rs = stmt.executeQuery();

            out.println("<html><body>");
            out.println("<h2>Courses Attended by Student ID: " + studentId + "</h2>");
            out.println("<ul>");
            while (rs.next()) {
                out.println("<li>" + rs.getString("name") + "</li>");
            }
            out.println("</ul>");
            out.println("<a href='attendance'>Search another student</a>");
            out.println("</body></html>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

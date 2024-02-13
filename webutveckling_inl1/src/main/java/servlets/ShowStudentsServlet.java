package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
@WebServlet("/students")
public class ShowStudentsServlet extends HttpServlet {

    // method to create a navbar
    private String navigationBar() {
        // returns html as a string
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

    // method to handle the get requests
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(navigationBar());
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:1337/gritacademy", "readuser", "");
            Statement st = conn.createStatement();
            // sql query that gets the information
            ResultSet rs = st.executeQuery("SELECT id, fname, lname, town, hobby FROM students");

            out.println("<html><body>");
            out.println("<h2>List of Students</h2>");
            out.println("<table border='1'><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Town</th><th>Hobby</th></tr>");
            // loops through the resultset, extracting data for each student and adding it to the table.
            while (rs.next()) {
                out.println("<tr><td>" + rs.getInt("id") + "</td><td>" + rs.getString("fname") + "</td><td>" + rs.getString("lname") + "</td><td>" + rs.getString("town") + "</td><td>" + rs.getString("hobby") + "</td></tr>");
            }
            out.println("</table>");
            out.println("</body></html>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

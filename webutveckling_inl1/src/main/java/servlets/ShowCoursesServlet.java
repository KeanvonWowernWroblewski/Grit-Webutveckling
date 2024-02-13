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

@WebServlet("/courses")
public class ShowCoursesServlet extends HttpServlet {

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

            // creates a statement object for sending sql statements to the database
            Statement st = conn.createStatement();
            // sql query to get the information from the database
            ResultSet rs = st.executeQuery("SELECT id, name, yhp, description FROM courses");

            out.println("<html><body>");
            out.println("<h2>List of Courses</h2>");
            out.println("<table border='1'><tr><th>ID</th><th>Name</th><th>YHP</th><th>Description</th></tr>");
            // loop through the resultset, extracting the course data and adding it to the table.
            while (rs.next()) {
                out.println("<tr><td>" + rs.getInt("id") + "</td><td>" + rs.getString("name") + "</td><td>" + rs.getInt("yhp") + "</td><td>" + rs.getString("description") + "</td></tr>");
            }
            out.println("</table>");
            out.println("</body></html>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

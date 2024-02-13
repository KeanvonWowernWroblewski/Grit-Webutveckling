package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/courses")
public class CoursesServlet extends HttpServlet {

    // method to create the navigation bar
    private String navigationBar() {
        // returns the html navigation bar as a string
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

    // handles the get request to show courses and provide a form for adding new courses
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>List of Courses</title></head><body>");
        out.println(navigationBar());

        // html form for adding a new course
        out.println("<h2>Add New Course</h2>");
        out.println("<form action='courses' method='POST'>");
        out.println("Name: <input type='text' name='name'><br>");
        out.println("YHP: <input type='number' name='yhp'><br>");
        out.println("Description: <textarea name='description'></textarea><br>");
        out.println("<input type='submit' value='Add Course'>");
        out.println("</form>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:1337/gritacademy", "readuser", "");

            Statement st = conn.createStatement();
            //  query to get all courses
            ResultSet rs = st.executeQuery("SELECT id, name, yhp, description FROM courses");

            out.println("<h2>List of Courses</h2>");
            out.println("<table border='1'><tr><th>ID</th><th>Name</th><th>YHP</th><th>Description</th></tr>");
            while (rs.next()) {
                // loops through result set and displays each course
                out.println("<tr><td>" + rs.getInt("id") + "</td><td>" + rs.getString("name") + "</td><td>" + rs.getInt("yhp") + "</td><td>" + rs.getString("description") + "</td></tr>");
            }
            out.println("</table>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        out.println("</body></html>");
    }

    // handles post request to add a new course to the database
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // gets course details from request parameters
        String name = request.getParameter("name");
        int yhp = Integer.parseInt(request.getParameter("yhp"));
        String description = request.getParameter("description");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:1337/gritacademy", "readuser", "");

            String query = "INSERT INTO courses (name, yhp, description) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setInt(2, yhp);
            ps.setString(3, description);

            ps.executeUpdate();
            response.sendRedirect("courses");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

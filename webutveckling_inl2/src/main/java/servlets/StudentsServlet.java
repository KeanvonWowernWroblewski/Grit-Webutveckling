package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/students")
public class StudentsServlet extends HttpServlet {
    private String getNavigationBar() {
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

    // handles get request to show students and creates a form for adding new students
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>List of Students</title></head><body>");
        out.println(getNavigationBar());

        out.println("<h2>Add New Student</h2>");
        out.println("<form action='students' method='POST'>");
        out.println("First Name: <input type='text' name='fname'><br>");
        out.println("Last Name: <input type='text' name='lname'><br>");
        out.println("Town: <input type='text' name='town'><br>");
        out.println("Hobby: <input type='text' name='hobby'><br>");
        out.println("<input type='submit' value='Add Student'>");
        out.println("</form>");

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:1337/gritacademy", "readuser", "");

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, fname, lname, town, hobby FROM students");

            out.println("<h2>List of Students</h2>");
            out.println("<table border='1'><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Town</th><th>Hobby</th></tr>");
            while (rs.next()) {
                out.println("<tr><td>" + rs.getInt("id") + "</td><td>" + rs.getString("fname") + "</td><td>" + rs.getString("lname") + "</td><td>" + rs.getString("town") + "</td><td>" + rs.getString("hobby") + "</td></tr>");
            }
            out.println("</table>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        out.println("</body></html>");
    }

    // handles post request to add a new student to the database
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // gets the student details from form the submission
        String fname = request.getParameter("fname");
        String lname = request.getParameter("lname");
        String town = request.getParameter("town");
        String hobby = request.getParameter("hobby");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:1337/gritacademy", "readuser", "");

            String query = "INSERT INTO students (fname, lname, town, hobby) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setString(3, town);
            ps.setString(4, hobby);

            ps.executeUpdate();

            response.sendRedirect("students");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

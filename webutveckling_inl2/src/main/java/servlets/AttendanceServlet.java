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
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Attendance Management</title></head><body>");
        out.println(navigationBar());

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:1337/gritacademy", "readuser", "");

            // gets students for listing, searching, and dropdown
            Statement stStudents = conn.createStatement();
            ResultSet rsStudents = stStudents.executeQuery("SELECT id, fname, lname FROM students");

            // gets courses for dropdown
            Statement stCourses = conn.createStatement();
            ResultSet rsCourses = stCourses.executeQuery("SELECT id, name FROM courses");

            // displays the list of students with ids for reference (was used before but decided to keep)
            out.println("<h2>List of Students</h2>");
            out.println("<ul>");
            while (rsStudents.next()) {
                out.println("<li>ID: " + rsStudents.getInt("id") + ", Name: " + rsStudents.getString("fname") + " " + rsStudents.getString("lname") + "</li>");
            }
            out.println("</ul>");
            rsStudents = stStudents.executeQuery("SELECT id, fname, lname FROM students");

            // form for searching a students courses
            out.println("<h2>Search for Student's Courses</h2>");
            out.println("<form action='attendance' method='POST'>");
            out.println("Student ID: <select name='studentId'>");
            while (rsStudents.next()) {
                out.println("<option value='" + rsStudents.getInt("id") + "'>" + rsStudents.getString("fname") + " " + rsStudents.getString("lname") + "</option>");
            }
            out.println("</select><br>");
            out.println("<input type='hidden' name='action' value='search'>");
            out.println("<input type='submit' value='Search'>");
            out.println("</form>");

            // assignment form with dropdowns
            out.println("<h2>Assign Student to Course</h2>");
            out.println("<form action='attendance' method='POST'>");

            // query to repopulate the resultset for student dropdown
            rsStudents = stStudents.executeQuery("SELECT id, fname, lname FROM students");

            out.println("Select Student: <select name='studentId'>");
            while (rsStudents.next()) {
                out.println("<option value='" + rsStudents.getInt("id") + "'>" + rsStudents.getString("fname") + " " + rsStudents.getString("lname") + "</option>");
            }
            out.println("</select><br>");

            out.println("Select Course: <select name='courseId'>");
            while (rsCourses.next()) {
                out.println("<option value='" + rsCourses.getInt("id") + "'>" + rsCourses.getString("name") + "</option>");
            }
            out.println("</select><br>");

            out.println("<input type='hidden' name='action' value='assign'>");
            out.println("<input type='submit' value='Assign'>");
            out.println("</form>");

            out.println("</body></html>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("assign".equals(action)) {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            int studentId = Integer.parseInt(request.getParameter("studentId"));

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:1337/gritacademy", "readuser", "");
                String query = "INSERT INTO attendance (`student.id`, `courses.id`) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, studentId);
                ps.setInt(2, courseId);
                ps.executeUpdate();

                response.sendRedirect("attendance");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("search".equals(action)) {
            doPostSearch(request, response);
        }
    }

    private void doPostSearch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentId = request.getParameter("studentId");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:1337/gritacademy", "readuser", "");

            PreparedStatement ps = conn.prepareStatement("SELECT c.name FROM courses c JOIN attendance a ON c.id = a.`courses.id` JOIN students s ON s.id = a.`student.id` WHERE s.id = ?");
            ps.setInt(1, Integer.parseInt(studentId));
            ResultSet rs = ps.executeQuery();

            out.println("<html><body>");
            out.println(navigationBar());
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

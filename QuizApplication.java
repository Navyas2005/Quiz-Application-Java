import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class QuizApplication {

    static String url = "jdbc:mysql://localhost:3306/quiz_db";
    static String username = "root";
    static String password = "Navyasree@142";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== QUIZ APPLICATION =====");
            System.out.println("1. Start Quiz");
            System.out.println("2. View Results");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {

                System.out.print("\nEnter your name: ");
                String name = sc.nextLine();

                Connection con = getConnection();

                int score = 0;
                int totalQuestions = 0;

                try {

                    String sql = "SELECT * FROM questions";

                    PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {

                        totalQuestions++;

                        System.out.println("\nQuestion " + totalQuestions + ": "
                                + rs.getString("question"));

                        System.out.println("A. " + rs.getString("option_a"));
                        System.out.println("B. " + rs.getString("option_b"));
                        System.out.println("C. " + rs.getString("option_c"));
                        System.out.println("D. " + rs.getString("option_d"));

                        String answer;

                        while (true) {

                            System.out.print("Enter your answer (A/B/C/D): ");
                            answer = sc.nextLine().toUpperCase();

                            if (answer.equals("A") || answer.equals("B")
                                    || answer.equals("C") || answer.equals("D")) {
                                break;
                            }

                            System.out.println(
                                    "Invalid answer! Please enter A, B, C or D.");
                        }

                        String correctAnswer = rs.getString("correct_answer");

                        if (answer.equals(correctAnswer)) {
                            System.out.println("Correct!");
                            score++;
                        } else {
                            System.out.println("Wrong!");
                        }
                    }

                    double percentage = ((double) score / totalQuestions) * 100;

                    String status;
                    String grade;

                    if (percentage >= 90) {
                        grade = "A";
                        status = "PASS";
                    } else if (percentage >= 75) {
                        grade = "B";
                        status = "PASS";
                    } else if (percentage >= 60) {
                        grade = "C";
                        status = "PASS";
                    } else if (percentage >= 40) {
                        grade = "D";
                        status = "PASS";
                    } else {
                        grade = "F";
                        status = "FAIL";
                    }

                    System.out.println("\n==============================");
                    System.out.println("         QUIZ RESULT");
                    System.out.println("==============================");
                    System.out.println("Name       : " + name);
                    System.out.println("Score      : " + score + "/" + totalQuestions);
                    System.out.printf("Percentage : %.2f%%%n", percentage);
                    System.out.println("Grade      : " + grade);
                    System.out.println("Status     : " + status);
                    System.out.println("==============================");

                    String insertSql = "INSERT INTO results "
                            + "(student_name, score, total_questions, percentage, status) "
                            + "VALUES (?, ?, ?, ?, ?)";

                    PreparedStatement insertPs = con.prepareStatement(insertSql);

                    insertPs.setString(1, name);
                    insertPs.setInt(2, score);
                    insertPs.setInt(3, totalQuestions);
                    insertPs.setDouble(4, percentage);
                    insertPs.setString(5, status);

                    insertPs.executeUpdate();

                    System.out.println("Result saved successfully!");

                    con.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (choice == 2) {

                Connection con = getConnection();

                try {

                    String sql = "SELECT * FROM results";

                    PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();

                    System.out.println("\n===== ALL RESULTS =====");

                    while (rs.next()) {

                        System.out.println("ID         : " + rs.getInt("id"));
                        System.out.println("Name       : "
                                + rs.getString("student_name"));
                        System.out.println("Score      : "
                                + rs.getInt("score") + "/"
                                + rs.getInt("total_questions"));

                        if (rs.getObject("percentage") != null) {
                            System.out.printf("Percentage : %.2f%%%n",
                                    rs.getDouble("percentage"));
                        } else {
                            System.out.println("Percentage : Not Available");
                        }

                        if (rs.getString("status") != null) {
                            System.out.println("Status     : "
                                    + rs.getString("status"));
                        } else {
                            System.out.println("Status     : Not Available");
                        }

                        System.out.println("------------------------------");
                    }

                    con.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (choice == 3) {

                System.out.println("\nThank you for using Quiz Application!");
                break;

            } else {

                System.out.println(
                        "Invalid choice! Please select 1, 2 or 3.");
            }
        }

        sc.close();
    }
}
package com.college.utils;

import com.college.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//צריך להגיד לשרת איך מתחברים למסדי הנתונים

@Component
public class DbUtils {
    private Connection connection;//יוצרים חיבור (קונקשן) בעצם ככה הקליינט מזדהה ואומר הנה היוזר שלי


    @PostConstruct//מתודה שתרוץ מיד על העלאת האפליקציה ופה ניצור את הקונקשן
    public void init () {
        String host = "localhost";
        String username = "root";//פה אנחנו נכתוב את המידע שכתבנו כשיצרנו את הדטהבייס
        String password = "123";//וזאת הסיסמה, (אני יצרתי 123 אבל אוה אם את קוראת תעשי 1234 כמו שי שלא יהיו לך שגיאות

        //שי בדק דרך הגוגל איזה סטרינג צריך לשים כדי לחבר לדטהבייס ופשוט שינה את הדברים שלו
        String url = "jdbc:mysql://localhost:3306/ash_2025?useSSL=false&serverTimezone=UTC";
        try {
            this.connection = DriverManager.getConnection(url, username, password);//עוטפים את זה בטריי קאץ', וכך אנחנו מאתחלים את השדה של המחלקה
            System.out.println("Connection established!");//אם זה מוצג - זה אומר שהצלחתי ליצור קשר עם הדטה בייס
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUserOnDb (User user) {//מקבלת מבחוץ יוזר ותשמור רשומה בדטה בייס
        //בתמונה 3+4 יש הסבר על איך הרצתי את השאילתא, ככה בדקתי שהיא בכלל עובדת
        try {
            PreparedStatement statement = this.connection.prepareStatement(//יוצר אובייקט שנקרא פריפרד סטייטמנט (שאילתא). כדי שנוכל אחר כך לשאול את הדטה בייס שאלה והוא יוכל להחזיר תשובה
                    "INSERT INTO users (first_name, last_name, phone, username)" +
                    "VALUE (?, ?, ?, ?)");//הסימני שאלה זה פלייסהולדרים
            statement.setString(1, user.getFirstName());//ככה ממלאים אותם, במיקום ה1 בתוך הסוגריים יהיה שם פרטי וכך הלאה..
            statement.setString(2, user.getLastName());//מיקום 2 שם משפחה..
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getUsername());
            statement.executeUpdate();//
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<User> getAllUsers () {
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement preparedStatement =
                    this.connection.prepareStatement("SELECT first_name, last_name, phone, username FROM users");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString(1);
                String lastName = resultSet.getString(2);
                String phone = resultSet.getString(3);
                String username = resultSet.getString(4);
                User user = new User(firstName, lastName, phone, username);
                users.add(user);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;

    }


}

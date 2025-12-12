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
    private Connection connection;//יוצרים חיבור (קונקשן) בעצם ככה הקליינט מזדהה ואומר הנה היוזר שלי וככה הקליינט יוכל לבצע שאילתות


    @PostConstruct//מתודה שתרוץ מיד על העלאת האפליקציה ופה ניצור את הקונקשן
    public void init () {
        String host = "localhost";
        String username = "root";//פה אנחנו נכתוב את המידע שכתבנו כשיצרנו את הדטהבייס
        String password = "123";//וזאת הסיסמה, (אני יצרתי 123 אבל אוה אם את קוראת תעשי 1234 כמו שי שלא יהיו לך שגיאות

        // כשאני אצור את הדאטה בייס ליצור אותו עם ססמה 1234 ולשנות פה ל1234

        //שי בדק דרך הגוגל איזה סטרינג צריך לשים כדי לחבר לדטהבייס ופשוט שינה את הדברים שלו
        String url = "jdbc:mysql://localhost:3306/ash_2025?useSSL=false&serverTimezone=UTC"; // זו המחרוזת שאיתה פותחים את הקונקשן למסד הנתונים - עם שינוי לשם הסכימה שיצרנו ash_2025 ולשנות את הפורט אם הגדרנו משהו אחר (בכללי אם מתקינים על אותו מחשב כמה מסדי נתונים עם פורטים שונים בעת ההתקנה של הספרייה אז ניתן מספרים עוקבים לפורטים עבור כל דאטה בייס)
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
                    "VALUE (?, ?, ?, ?)");//הסימני שאלה זה פלייסהולדרים (ה-?) כדי שלא יהיה הארד קודד עם ערכים קבועים
            statement.setString(1, user.getFirstName());//ככה ממלאים אותם, במיקום ה1 בתוך הסוגריים יהיה שם פרטי וכך הלאה..
            statement.setString(2, user.getLastName());//מיקום 2 שם משפחה..
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getUsername());
            statement.executeUpdate();//
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }     // בכללי: פרפר סטייטמנט זה לא שאילתה באמת - זו פשוט בקשה שלי מהמסד נתונים. אבל ככה קוראים לזה בערית מה לעשות :/ .


    // מה שיחזור לי בקונטרולר - אחרי שכבר חיברנו את זה לדאטה בייס שינינו שלא תחזור סתם רשימת היוזרים כמו שהיה מקודם אלא רשימת היוזרים שקיימת בטבלת היוזרים בדאטה בייס
    // זה מה שיוצג בנתיב /all כששולחים בקשה לשרת כדי לקבל את כל היוזרים (מה שכתבנו בשיעור הקודם ומה שדיברנו עליו ממה שבחילת השיעור
    public List<User> getAllUsers () {
        List<User> users = new ArrayList<>();//רשימה ריקה
        try {
            PreparedStatement preparedStatement =
                    this.connection.prepareStatement("SELECT first_name, last_name, phone, username FROM users");
            ResultSet resultSet = preparedStatement.executeQuery();//מאחורי הקלעים זה ממומש כסוג של רשימה מקושרת
            while (resultSet.next()) {//כל פעם מביאה לי רשומה אחת, שורה אחת מתוך הדאטהבייס
                String firstName = resultSet.getString(1);
                String lastName = resultSet.getString(2);
                String phone = resultSet.getString(3);
                String username = resultSet.getString(4); // שי הגדיר את זה כשם פרטי בשיעור כי לא חייבנו שיכניסו יוזרניים ושמנו ״״ ולכן שומר מחרוזת ריקה שזה לא חוקי בטבלאות - והרי שי הגדיר אצלו שעמודת היוזרניים היא unique key אז לא חוקי שיהיה ריק - הראה לו שגיאה של יוניק קי.
                // תזכורת: ככה שלחנו לשרת בקשה מצד לקוח:
                //  axios.get("http://localhost:8989/create-user?first=" + firstName + "" +
                //                                "&last=" + lastName + "&phone=1234567")
                // אין פה יוזרניים...
                User user = new User(firstName, lastName, phone, username);//כל רשומה יוצרים לה יוזר
                users.add(user);//מוסיפים לתוך הרשימה

            }
        } catch (SQLException e) {//אין פה אקסקיוט אפדייט כי אני לא מעדכנת כלום.. מה שכן אני מביאה שאילתא
            throw new RuntimeException(e);
        }
        return users;
    }


}

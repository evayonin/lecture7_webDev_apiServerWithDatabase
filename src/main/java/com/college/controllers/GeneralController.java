package com.college.controllers;


import com.college.AllUsersResponse;
import com.college.BasicResponse;
import com.college.User;
import com.college.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.college.utils.Errors.ERROR_MISSING_FIRST_NAME;
import static com.college.utils.Errors.ERROR_MISSING_LAST_NAME;


@RestController
public class GeneralController {//קונטרולר->מחלקה שמאזינה לבקשות
    private List<User> allUsers = new ArrayList<>();//כל הרשימה הזאת הולכת לפח כשמאתחלים את השרת...
    //אז נרצה קונסיסטנסי, לשמור את המידע הזה בקובץ

    @Autowired//אנוטציה מוכרת של דיבייוטילס
    private DbUtils dbUtils;

    @PostConstruct
    public void init () {
    }

    @RequestMapping("/all")//בבקשה מחזירה איזשהו ג'ייסון
    public BasicResponse getAllUsers () {
        return new AllUsersResponse(
                true,
                null,
                dbUtils.getAllUsers());
    }

    //אפליקציית פוסטמן- מיועדת על מנת לשלוח בקשות API, יותר נוחה מאשר להקליד נתונים בתוך הURL בדפדפן
    //היום נעסוק באיך להעביר את המידע שיש לנו לזיכרון שהוא לא נדיף (אם נאתחל את השרת כל פעם, המידע שלנו נעלם ואנחנו לא נרצה את זה)

    @RequestMapping("create-user")
    public BasicResponse addUser (String first, String last, String phone) {
        if (first != null && !first.isEmpty()) {//רק אם יש בפנים איזשהו תוכן, ואנחנו נוסיף אותה בכל מקרה בצד שרת כי אני לא מכיר את הקליינט, אני לא יודעת איזה הגנות הם יעשו/או לא יעשו ולכן אני אעדיף תמיד לעשות הגנה כפולה (לא נסמוך על הקליינט)
            if (last != null && !last.isEmpty()) {
                User user = new User(first, last, phone, "");//הוא עשה פה מחרוזת ריקה כדי שלא תהיה שגיאה (אנחנו לא הגדרנו בכלל שם משתמש)
                 dbUtils.createUserOnDb(user);//פה הוא פונה למחלקה הזאת ויוצר את היוזר הזה בדטה בייס
                return new BasicResponse(true, null);
            } else {
                return new BasicResponse(false, ERROR_MISSING_LAST_NAME);
            }
        } else {
            return new BasicResponse(false, ERROR_MISSING_FIRST_NAME);
        }
    }




}

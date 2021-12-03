
package edu.oakland;

import android.app.WallpaperInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    private static Context myContext;
    private final File DB_FILE;
    private static String DB_PATH = "/data/data/edu.oakland/databases/";
    private static String DB_NAME = "Database";


    private SQLiteDatabase db;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        DB_FILE = context.getDatabasePath(DB_NAME);
        this.myContext = context;

    }


    public void createDatabase() throws IOException{

        boolean dbExist = checkDatabase();
        if(dbExist)
        {
            System.out.println("Database Exists");
            //do nothing - database already exist
        }
        else{
            this.getReadableDatabase();
            try{
                copyDatabase();
            }catch (IOException e)
            {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDatabase()
    {
        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch (SQLException e)
        {
            //database does not exist yet.
        }

        if(checkDB != null)
        {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDatabase() throws IOException
    {
        //Open local db as input file
        InputStream myInput = myContext.getAssets().open(DB_NAME + ".db");

        //Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db file that was created by DBHelper as an output file
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while(( length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams or the output file
        myOutput.flush(); // write to the output file
        myOutput.close();
        myInput.close();

    }

    public void openDatabase() throws SQLException
    {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if(db != null)
        {
            db.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

//    public Boolean insertData(String phone)
//    {
//        SQLiteDatabase myDB = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("phone", phone);
//        long result = myDB.insert("users", null, contentValues);
//
//        if(result == -1)
//        {
//            return false;
//        }else{
//            return true;
//        }
//    }

    public Boolean checkPhone(String phoneNo)
    {
        SQLiteDatabase myDB = this.getReadableDatabase();
        //String query = "select * from users where phone = ?" + phone;
        Cursor cursor = myDB.rawQuery("SELECT * FROM users where phone = ?",new String[] {phoneNo});

        if(cursor.getCount() > 0)
        {
            return true;
        }else{
            return false;
        }
    }

    public Boolean signUp(String fName, String lName, String email, String phoneNo, String address, String zip, String state , String city)
    {
        SQLiteDatabase myDB = this.getWritableDatabase();
        //String query = "select * from users where phone = ?" + phone;
        String insertCommentQuery = "insert into users (fName, lName, email, phone, address, city, state, zipCode) " +
                "values ('" + fName + "','" + lName + "','" + email + "','" + phoneNo + "','" +address + "','" + city + "','" + state + "','" + zip + "')";


        myDB.execSQL(insertCommentQuery);

        Cursor cursor = myDB.rawQuery("SELECT * FROM users where phone = ?",new String[] {phoneNo});

        if(cursor.getCount() > 0)
        {
            return true;
        }else{
            return false;
        }
    }

    public Boolean insertPreference(String phone, int experience, String gender, String isVaccinated, String isPetFriendly, String isTrans, String isnonSmoker , String isFirstAid)
    {
        SQLiteDatabase myDB = this.getWritableDatabase();
        //String query = "select * from users where phone = ?" + phone;
        String insertCommentQuery = "insert into preference (phone, experience, gender, isVaccinated, isPetFriendly, isTrans, isnonSmoker, isFirstAid) " +
                "values ('" + phone + "','" + experience + "','" + gender + "','" + isVaccinated + "','" +isPetFriendly + "','" + isTrans + "','" + isnonSmoker + "','" + isFirstAid + "')";


        myDB.execSQL(insertCommentQuery);

        Cursor cursor = myDB.rawQuery("SELECT * FROM preference where phone = ?",new String[] {phone});

        if(cursor.getCount() > 0)
        {
            return true;
        }else{
            return false;
        }
    }

    public Boolean updatePreference(String phone, int experience, String gender, String isVaccinated, String isPetFriendly, String isTrans, String isnonSmoker , String isFirstAid)
    {
        SQLiteDatabase myDB = this.getWritableDatabase();
        //String query = "select * from users where phone = ?" + phone;
        String updateCommentQuery = "update preference set experience = " + experience + "," + " gender = '" + gender + "'," + " isVaccinated = '" + isVaccinated + "',"  + " isPetFriendly = '" + isPetFriendly + "'," + " isTrans = '" + isTrans + "'," + " isnonSmoker = '" + isnonSmoker + "'," + " isFirstAid = '" + isFirstAid + "'"
        + " where phone ='" + phone + "'";


        myDB.execSQL(updateCommentQuery);

        Cursor cursor = myDB.rawQuery("SELECT * FROM preference where phone = '" + phone + "' and " + " gender = '" + gender + "' and " + " isVaccinated = '" + isVaccinated + "' and "  + " isPetFriendly = '" + isPetFriendly + "' and " + " isTrans = '" + isTrans + "' and " + " isnonSmoker = '" + isnonSmoker + "' and " + " isFirstAid = '" + isFirstAid + "'",null);

        if(cursor.getCount() > 0)
        {
            return true;
        }else{
            return false;
        }
    }



    public Cursor checkUserPreference(String phoneNo)
    {
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT phone, experience, gender, isVaccinated, isPetFriendly, isTrans, isnonSmoker, isFirstAid FROM preference where phone = ?",new String[] {phoneNo});

        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            return cursor;
        }else{
            return null;
        }
    }

    public Cursor checkZipCity(String phoneNo)
    {
        SQLiteDatabase myDB = this.getReadableDatabase();
        //String query = "select * from users where phone = ?" + phone;
        Cursor cursor = myDB.rawQuery("SELECT zipCode, city FROM users where phone = ?",new String[] {phoneNo});

        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            return cursor;
        }else{
            return null;
        }
    }

    public Cursor extractNannies(String phoneNo, String zipcodes)
    {
        SQLiteDatabase myDB = this.getReadableDatabase();

         /*
        String query = "SELECT A.Phone , A.image, A.F_Name, A.L_Name, A.Experience, A.City, Round(Avg(B.rating)) as Rating\n"+
                "from nanny A\n" +
                "inner join nanny_quality C on C.phone = A.Phone\n" +
                "inner join preference D on C.isVaccinated = D.isVaccinated and C.isPetFriendly = D.isPetFriendly and C.isTrans = D.isTrans and C.isnonSmoker = D.isnonSmoker and C.isFirstAid = D.isFirstAid\n" +
                "left join ratings B on A.Phone = B.phone\n" +
                "where D.phone ='" + phoneNo + "' and A.experience >= D.experience and A.ZIP IN (" + zipcodes + ")\n" +
                "group by A.Phone , A.image, A.F_Name, A.L_Name, A.Experience, A.City order by Avg(B.rating) Desc ";

         */

        String query = "SELECT A.Phone , A.image, A.F_Name, A.L_Name, A.Experience, A.City, Round(Avg(B.rating)) as Rating\n"+
                "from nanny A\n" +
                "inner join nanny_quality C on C.phone = A.Phone\n" +
                "inner join preference D on A.Gender = D.gender and C.isVaccinated = D.isVaccinated and C.isPetFriendly = D.isPetFriendly and C.isTrans = D.isTrans and C.isnonSmoker = D.isnonSmoker and C.isFirstAid = D.isFirstAid\n" +
                "left join ratings B on A.Phone = B.phone\n" +
                "where D.phone ='" + phoneNo + "' and A.experience >= D.experience and A.ZIP IN (" + zipcodes + ")\n" +
                 "and A.Phone not in("
                +"select Nanny_phone from Booking where Scheduled_Date = ?"+
                "and Scheduled_StartTime= ? and Scheduled_EndTime= ? and Scheduled_Date >=  date('now')\n" +
                ")"+
                "group by A.Phone , A.image, A.F_Name, A.L_Name, A.Experience, A.City order by Avg(B.rating) Desc ";

        Cursor cursor = myDB.rawQuery(query,new String[]{"11/28/2021","01:00","02:00"});
        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            return cursor;
        }else{
            return null;
        }
    }

    public Cursor getNannyDetails(String phoneNo)
    {
        SQLiteDatabase myDB = this.getReadableDatabase();
       // Cursor cursor = myDB.rawQuery("SELECT zipCode, city FROM users where phone = ?",new String[] {phoneNo});
        String queryForNannies = "SELECT A.Phone , A.image, A.F_Name, A.L_Name, A.Experience, A.City, Round(Avg(B.rating)) as Rating, C.isVaccinated, C.isPetFriendly, C.isTrans, C.isnonSmoker, C.isFirstAid\n" +
        "from nanny A\n" +
        "inner join nanny_quality C on C.phone = A.Phone\n" +
        "left join ratings B on A.Phone = B.phone\n" +
        "where A.phone = '" + phoneNo + "'\n" +
        "group by A.Phone , A.image, A.F_Name, A.L_Name, A.Experience, A.City order by Round(Avg(B.rating)) Desc";



        Cursor cursor = myDB.rawQuery(queryForNannies,null);

        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            return cursor;
        }else{
            return null;
        }
    }

    public int createBooking(String User_phone, String Nanny_phone, String Scheduled_Date, String Scheduled_StartTime, String Scheduled_EndTime, int No_Newborn, int No_Toddler, int No_EarlySchool, int No_School, int PaymentMode, int RatePerHour)
    {
        SQLiteDatabase myDB = this.getWritableDatabase();
        //String query = "select * from users where phone = ?" + phone;
        String insertCommentQuery = "insert into Booking (BookingID, User_phone, Nanny_phone, Scheduled_Date, Scheduled_StartTime, Scheduled_EndTime, No_Newborn, No_Toddler, No_EarlySchool, No_School, PaymentMode, RatePerHour) " +
              "values (null,'" + User_phone + "','" + Nanny_phone + "','" + Scheduled_Date + "','" + Scheduled_StartTime + "','" +Scheduled_EndTime + "'," + No_Newborn + "," + No_Toddler + "," + No_EarlySchool + "," + No_School + "," + PaymentMode + "," + RatePerHour + ")";

        myDB.execSQL(insertCommentQuery);

        Cursor cursor = myDB.rawQuery("SELECT max(BookingID) FROM Booking",null);

        cursor.moveToFirst();
        int bookingID = cursor.getInt(0);

        return bookingID;
        }

    public Cursor getUserInfo(String phoneNo)
    {
        SQLiteDatabase myDB = this.getReadableDatabase();
        //String query = "select * from users where phone = ?" + phone;
        Cursor cursor = myDB.rawQuery("SELECT * FROM users where phone = ?",new String[] {phoneNo});

        cursor.moveToFirst();
        return cursor;
    }

    public Cursor confirmBooking(int BookingID)
    {
        SQLiteDatabase myDB = this.getReadableDatabase();
        // Cursor cursor = myDB.rawQuery("SELECT zipCode, city FROM users where phone = ?",new String[] {phoneNo});
        String queryConfirmBooking = "SELECT A.BookingID, A.Scheduled_StartTime, A.Scheduled_EndTime, A.Nanny_phone, B.F_Name, B.L_Name, B.Image, C.address, C.city, C.state, C.zipcode, A.Scheduled_Date\n" +
        " FROM Booking A \n" +
        "inner join Nanny B on A.Nanny_phone = B.phone \n " +
        "inner join users C on A.User_phone = C.phone" + " WHERE A.BookingID = " + BookingID ;


        Cursor cursor = myDB.rawQuery(queryConfirmBooking,null);

        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            return cursor;
        }else{
            return null;
        }
    }

    public Cursor extractBookings(String phoneNo)
    {
        SQLiteDatabase myDB = this.getReadableDatabase();
        String query = "SELECT A.BookingID , B.F_Name, B.L_Name, B.Image, A.Scheduled_Date \n"+
                " FROM Booking A \n" +
                "inner join Nanny B on A.Nanny_phone = B.phone \n " +
                "where A.User_phone ='" + phoneNo + "' \n" +
                "order by A.Scheduled_Date Desc";

        System.out.println("Extract Bookings = " + query);

        Cursor cursor = myDB.rawQuery(query,null);
        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            return cursor;
        }else{
            return null;
        }
    }

    public ArrayList<String> getBookedNanny(String Scheduled_Date, String Scheduled_StartTime, String Scheduled_EndTime)
    {
        ArrayList<String> bookedNannies = new ArrayList<>();
        SQLiteDatabase myDB = this.getReadableDatabase();
        // Cursor cursor = myDB.rawQuery("SELECT zipCode, city FROM users where phone = ?",new String[] {phoneNo});
        String queryForNannies = "select Phone from  nanny where Phone in(\n" +
                "select Nanny_phone from Booking where Scheduled_Date = ?" +
                "and Scheduled_StartTime= ? and Scheduled_EndTime= ? and Scheduled_Date >=  date('now')\n" +
                ")";
        Cursor cursor = myDB.rawQuery(queryForNannies,new String[]{Scheduled_Date,Scheduled_StartTime,Scheduled_EndTime});

        if(cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                bookedNannies.add(cursor.getString(0));
            }
        }
        return  bookedNannies;
    }
    }




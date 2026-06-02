package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import models.Expense;
import models.Product;
import models.Sale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LiquorPOS.db";
    private static final int DATABASE_VERSION = 10;

    // TABLES
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_SALES = "sales";
    private static final String TABLE_EXPENSES = "expenses";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // USERS TABLE
        db.execSQL(
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "full_name TEXT," +
                        "username TEXT UNIQUE," +
                        "email TEXT UNIQUE," +
                        "password TEXT," +
                        "role TEXT DEFAULT 'customer'," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")"
        );

        // PRODUCTS TABLE
        db.execSQL(
                "CREATE TABLE products (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "category TEXT," +
                        "buying_price REAL," +
                        "selling_price REAL," +
                        "stock INTEGER" +
                        ")"
        );

        // SALES TABLE
        db.execSQL(
                "CREATE TABLE sales (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "product_id INTEGER," +
                        "product_name TEXT," +
                        "quantity INTEGER," +
                        "price REAL," +
                        "total REAL," +
                        "date TEXT" +
                        ")"
        );

        // EXPENSES TABLE
        db.execSQL(
                "CREATE TABLE expenses (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT," +
                        "amount REAL," +
                        "date TEXT" +
                        ")"
        );


        // DEFAULT ADMIN
        ContentValues admin = new ContentValues();

        admin.put("full_name", "System Admin");
        admin.put("username", "admin");
        admin.put("email", "admin@system.com");
        admin.put("password", "1234");
        admin.put("role", "admin");

        db.insert(TABLE_USERS, null, admin);
        db.execSQL("DROP TABLE IF EXISTS expenses");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS sales");
        db.execSQL("DROP TABLE IF EXISTS expenses");

        onCreate(db);
    }

    // ================= USERS =================

    public boolean registerUser(
            String username,
            String email,
            String password,
            String role
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("role", role);

        long result =
                db.insert(TABLE_USERS, null, values);

        return result != -1;
    }

    public boolean loginUser(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password}
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }

    public String getUserRole(String username) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT role FROM users WHERE username=?",
                new String[]{username}
        );

        String role = "";

        if(cursor.moveToFirst()) {
            role = cursor.getString(0);
        }

        cursor.close();

        return role;
    }

    // ================= PRODUCTS =================

    public boolean addProduct(
            String name,
            String category,
            double buying,
            double selling,
            int stock
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("category", category);
        values.put("buying_price", buying);
        values.put("selling_price", selling);
        values.put("stock", stock);

        long result =
                db.insert(TABLE_PRODUCTS, null, values);

        return result != -1;
    }

    public ArrayList<Product> getAllProducts() {

        ArrayList<Product> list =
                new ArrayList<>();

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM products",
                        null
                );

        if(cursor.moveToFirst()) {

            do {

                Product p = new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getInt(5)
                );

                list.add(p);

            } while(cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    public void deleteProduct(int id) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        db.delete(
                TABLE_PRODUCTS,
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    public void reduceStock(
            int productId,
            int quantitySold
    ) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        db.execSQL(
                "UPDATE products SET stock = stock - ? WHERE id=?",
                new Object[]{
                        quantitySold,
                        productId
                }
        );
    }

    // ================= LOW STOCK =================

    public ArrayList<Product> getLowStockProducts() {

        ArrayList<Product> list =
                new ArrayList<>();

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM products WHERE stock <= 5",
                null
        );

        if(cursor.moveToFirst()) {

            do {

                Product p = new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getInt(5)
                );

                list.add(p);

            } while(cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    public boolean updateProduct(
            int id,
            String name,
            String category,
            double buyingPrice,
            double sellingPrice,
            int stock
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("buying_price", buyingPrice);
        values.put("selling_price", sellingPrice);
        values.put("stock", stock);

        int result = db.update(
                "products",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    public void saveSale(
            int productId,
            String productName,
            int quantity,
            double price,
            double total,
            String date
    ) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("product_id", productId);
        values.put("product_name", productName);
        values.put("quantity", quantity);
        values.put("price", price);
        values.put("total", total);
        values.put("date", date);

        db.insert(TABLE_SALES, null, values);
    }

    public ArrayList<Sale> getAllSales() {

        ArrayList<Sale> list =
                new ArrayList<>();

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM sales ORDER BY id DESC",
                null
        );

        if(cursor.moveToFirst()) {

            do {

                Sale s = new Sale();

                s.setId(cursor.getInt(0));
                s.setProductId(cursor.getInt(1));
                s.setProductName(cursor.getString(2));
                s.setQuantity(cursor.getInt(3));
                s.setPrice(cursor.getDouble(4));
                s.setTotal(cursor.getDouble(5));
                s.setDate(cursor.getString(6));

                list.add(s);

            } while(cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    public double getTotalSales() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(total) FROM sales",
                null
        );

        double total = 0;

        if(cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();

        return total;
    }

    // ================= EXPENSES =================

    public boolean addExpense(
            String title,
            double amount,
            String date
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("amount", amount);
        values.put("date", date);

        try {
            long result = db.insertOrThrow("expenses", null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace(); // 🔥 THIS WILL SHOW EXACT ERROR
            return false;
        }
    }

    public ArrayList<Expense> getAllExpenses() {

        ArrayList<Expense> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM expenses ORDER BY id DESC",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                Expense expense = new Expense();

                expense.setId(cursor.getInt(0));
                expense.setTitle(cursor.getString(1));
                expense.setAmount(cursor.getDouble(2));
                expense.setDate(cursor.getString(3));

                list.add(expense);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    // ================= DATE =================

    public String getCurrentDateTime() {

        return new SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
        ).format(new Date());
    }
}


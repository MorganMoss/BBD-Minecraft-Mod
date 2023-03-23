package za.co.bbd.minecraft.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database {
    private Connection conn = null;

    public Database(){}

    public void setupConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://172.23.122.70:5433/mcback", "postgres", "postgres");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getNbt(String name) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM public.backs WHERE name = '" + name + "';");
            rs.next();
            String s = rs.getString("nbt");
            return s;
        } catch (Exception e) {
            System.out.println(e);
            return "{Items:[{Count:32b,Slot:0b,id:\"minecraft:birch_planks\"},{Count:32b,Slot:1b,id:\"minecraft:oak_wood\"}]}";
        }
    }
    public void setNbt(String name, String nbt) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("UPDATE public.backs SET nbt='" + nbt + "' WHERE name='" + name + "';");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

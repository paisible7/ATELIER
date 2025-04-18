package com.example.service;

import com.example.app.Produit;
import com.example.user.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/supermarche";
    private static final String USER = "root";
    private static final String PASSWORD = "admin123mdp";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static List<Produit> chargerProduits(){
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits";

        try {
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return produits;
    }
}

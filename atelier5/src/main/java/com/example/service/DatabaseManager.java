package com.example.service;

import com.example.app.Produit;
import com.example.app.ProduitAlimentaire;
import com.example.app.ProduitCosmetique;
import com.example.app.ProduitMenager;
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

    public static List<Produit> chargerProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits";

        try (
                Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rslt = stmt.executeQuery(sql)) {
            while (rslt.next()) {
                String type = rslt.getString("type");
                Produit p = creerProduit(rslt, type);
                produits.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produits;
    }

    private static Produit creerProduit(ResultSet rslt, String type) throws SQLException {

        switch (type.toLowerCase()) {
            case "alimentaire":
                return new ProduitAlimentaire(
                        rslt.getInt("id"),
                        rslt.getString("nom"),
                        rslt.getDouble("prix"),
                        rslt.getInt("quantite"),
                        rslt.getString("date_expiration"),
                        rslt.getString("origine"));
            case "menager":
                return new ProduitMenager(
                        rslt.getInt("id"),
                        rslt.getString("nom"),
                        rslt.getDouble("prix"),
                        rslt.getInt("quantite"),
                        rslt.getString("type_menager"),
                        rslt.getString("date_fabrication"));
            case "cosmetique":
                return new ProduitCosmetique(
                        rslt.getInt("id"),
                        rslt.getString("nom"),
                        rslt.getDouble("prix"),
                        rslt.getInt("quantite"),
                        rslt.getString("type_peau"),
                        rslt.getBoolean("teste_sur_animaux"));
            default:
                return new Produit(
                        rslt.getInt("id"),
                        rslt.getString("nom"),
                        rslt.getDouble("prix"),
                        rslt.getInt("quantite"));
        }
    }
}

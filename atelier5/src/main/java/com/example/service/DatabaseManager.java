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

    public static void ajouterProduit(Produit produit) {
        String sql = "INSERT INTO produits (id, nom, prix, quantite, type, "
                + "date_expiration, origine, type_menager, date_fabrication, type_peau, teste_sur_animaux) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setCommonParameters(pstmt, produit);
            setTypeSpecificParameters(pstmt, produit);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void setCommonParameters(PreparedStatement pstmt, Produit produit) throws SQLException {
        pstmt.setInt(1, produit.getId());
        pstmt.setString(2, produit.getNom());
        pstmt.setDouble(3, produit.getPrix());
        pstmt.setInt(4, produit.getQuantite());
    }

    private static void setTypeSpecificParameters(PreparedStatement pstmt, Produit produit) throws SQLException {
        if (produit instanceof ProduitAlimentaire pa) {
            pstmt.setString(5, "alimentaire");
            pstmt.setString(6, pa.getDateExpiration());
            pstmt.setString(7, pa.getOrigine());
            pstmt.setNull(8, Types.VARCHAR);
            pstmt.setNull(9, Types.VARCHAR);
            pstmt.setNull(10, Types.VARCHAR);
            pstmt.setNull(11, Types.BOOLEAN);
        } else if (produit instanceof ProduitMenager pm) {
            pstmt.setString(5, "ménager");
            pstmt.setNull(6, Types.VARCHAR);
            pstmt.setNull(7, Types.VARCHAR);
            pstmt.setString(8, pm.getTypeProduit());
            pstmt.setString(9, pm.getDateFabrication());
            pstmt.setNull(10, Types.VARCHAR);
            pstmt.setNull(11, Types.BOOLEAN);
        } else if (produit instanceof ProduitCosmetique pc) {
            pstmt.setString(5, "cosmétique");
            pstmt.setNull(6, Types.VARCHAR);
            pstmt.setNull(7, Types.VARCHAR);
            pstmt.setNull(8, Types.VARCHAR);
            pstmt.setNull(9, Types.VARCHAR);
            pstmt.setString(10, pc.getTypePeau());
            pstmt.setBoolean(11, pc.getTesteSurAnimaux());
        }
    }

    public static void modifierProduit(Produit produit) {
        String sql = "UPDATE produits SET nom = ?, prix = ?, quantite = ?, "
                + "date_expiration = ?, origine = ?, type_menager = ?, date_fabrication = ?, "
                + "type_peau = ?, teste_sur_animaux = ? WHERE id = ?";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setUpdateParameters(pstmt, produit);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void setUpdateParameters(PreparedStatement pstmt, Produit produit) throws SQLException {
        pstmt.setString(1, produit.getNom());
        pstmt.setDouble(2, produit.getPrix());
        pstmt.setInt(3, produit.getQuantite());

        if (produit instanceof ProduitAlimentaire pa) {
            pstmt.setString(4, pa.getDateExpiration());
            pstmt.setString(5, pa.getOrigine());
            pstmt.setNull(6, Types.VARCHAR);
            pstmt.setNull(7, Types.VARCHAR);
            pstmt.setNull(8, Types.VARCHAR);
            pstmt.setNull(9, Types.BOOLEAN);
        } else if (produit instanceof ProduitMenager pm) {
            pstmt.setNull(4, Types.VARCHAR);
            pstmt.setNull(5, Types.VARCHAR);
            pstmt.setString(6, pm.getTypeProduit());
            pstmt.setString(7, pm.getDateFabrication());
            pstmt.setNull(8, Types.VARCHAR);
            pstmt.setNull(9, Types.BOOLEAN);
        } else if (produit instanceof ProduitCosmetique pc) {
            pstmt.setNull(4, Types.VARCHAR);
            pstmt.setNull(5, Types.VARCHAR);
            pstmt.setNull(6, Types.VARCHAR);
            pstmt.setNull(7, Types.VARCHAR);
            pstmt.setString(8, pc.getTypePeau());
            pstmt.setBoolean(9, pc.getTesteSurAnimaux());
        }
        pstmt.setInt(10, produit.getId());
    }

    public static void supprimerProduit(int id) {
        String sql = "DELETE FROM produits WHERE id = ?";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

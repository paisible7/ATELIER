package com.example.app;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.example.service.DatabaseManager;
import com.example.service.JsonManager;
import com.example.user.*;
public class SupermarcheApp {
    private static List<Produit> produits = DatabaseManager.chargerProduits();
    private static Scanner scanner = new Scanner(System.in);
    private static UserRole userRole = new UserRole();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Sauvegarde des produits avant de quitter...");
            JsonManager.sauvegarderProduits(produits);
        }));

        User user = authUser();
        if (user == null) {
            System.out.println("Nom d'utilisateur ou mot de passe incorrect.");
            return;
        }
        System.out.println("Connexion réussie en tant que " + user.getRole());
        System.out.println("==========================");
        System.out.println("Bienvenue " + user.getNom() + "!");

        int choix;
        do {
            System.out.println("Menu :");
            System.out.println("1. Ajouter un produit");
            System.out.println("2. Afficher tous les produits");
            System.out.println("3. Modifier un produit");
            System.out.println("4. Supprimer un produit");
            System.out.println("5. Quitter");
            System.out.print("Choisissez une option : ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    ajouterProduit();
                    break;
                case 2:
                    afficherProduits();
                    break;
                case 3:
                    modifierProduit();
                    break;
                case 4:
                    supprimerProduit();
                    break;
                case 5:
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        } while (choix != 5);
    }

    private static User authUser() {
        System.out.print("Nom d'utilisateur : ");
        String nom = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String motDePasse = scanner.nextLine();
        return userRole.authenticate(nom, motDePasse);
    }

    private static void ajouterProduit() {

        try{


        System.out.print("Entrez l'ID du produit : ");
        int id = scanner.nextInt();
        scanner.nextLine(); 
        System.out.print("Entrez le nom du produit : ");
        String nom = scanner.nextLine();
        System.out.print("Entrez le prix du produit : ");
        double prix = scanner.nextDouble();
        System.out.print("Entrez la quantité du produit : ");
        int quantite = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("Entrez le type de produit (alimentaire, ménager, cosmétique) : ");
        String type = scanner.nextLine();

        Produit produit;
        switch (type.toLowerCase()) {
            case "alimentaire":
                System.out.print("Entrez la date d'expiration : ");
                String dateExpiration = scanner.nextLine();
                System.out.print("Entrez l'origine : ");
                String origine = scanner.nextLine();
                produit = new ProduitAlimentaire(id, nom, prix, quantite, dateExpiration, origine);
                break;
            case "ménager":
                System.out.print("Entrez le type : ");
                String typeMenager = scanner.nextLine();
                System.out.print("Entrez la date de fabrication : ");
                String dateFabrication = scanner.nextLine();
                produit = new ProduitMenager(id, nom, prix, quantite, typeMenager, dateFabrication);
                break;
            case "cosmétique":
                System.out.print("Entrez le type de peau : ");
                String typePeau = scanner.nextLine();
                System.out.print("Est-il testé sur animaux ? (true/false) : ");
                boolean testeSurAnimaux = scanner.nextBoolean();
                produit = new ProduitCosmetique(id, nom, prix, quantite, typePeau, testeSurAnimaux);
                break;
            default:
                System.out.println("Type de produit invalide.");
                return;
        }
        produits.add(produit);
        DatabaseManager.ajouterProduit(produit);
        System.out.println("Produit ajouté avec succès !");
    }catch(InputMismatchException e){
        System.out.println("Erreur : entrée invalide. veuillez réessayer");
        scanner.nextLine();
    }
    }
    

    private static void afficherProduits() {
        if (produits.isEmpty()) {
            System.out.println("Aucun produit à afficher.");
            return;
        }
        for (Produit produit : produits) {
            produit.afficherDetails();
            System.out.println("-------------------------");
        }
    }

    private static void modifierProduit() {
        System.out.print("Entrez l'ID du produit à modifier : ");
        int id = scanner.nextInt();
        scanner.nextLine(); 
        boolean produitTrouve = false;
        for (Produit produit : produits) {
            if (produit.id == id) {
                produitTrouve = true;
                System.out.print("Entrez le nouveau nom : ");
                produit.nom = scanner.nextLine();
                System.out.print("Entrez le nouveau prix : ");
                produit.prix = scanner.nextDouble();
                if (produit.prix < 0) {
                    System.out.println("le prix ne peut pas etre negatif");
                    return;
                }
                produit.setPrix(produit.prix);
                System.out.print("Entrez la nouvelle quantité : ");
                produit.quantite = scanner.nextInt();
                if (produit.quantite < 0) {
                    System.out.println("La quantité ne peut pas être négative.");
                    return;
                }
                produit.setQuantite(produit.quantite);
                DatabaseManager.modifierProduit(produit);
                System.out.println("Produit modifié avec succès !");
                return;
            }
        }
        if (!produitTrouve) {
            System.out.println("Produit non trouvé.");
        }
    }

    private static void supprimerProduit() {
        System.out.print("Entrez l'ID du produit à supprimer : ");
        int id = scanner.nextInt();
        boolean produitSupprime = produits.removeIf(produit -> produit.getId() == id);
        DatabaseManager.supprimerProduit(id);

        if(produitSupprime){
            System.out.println("produit supprimé avec succès !");
        }
        else {
            System.out.println("Produit noon trouvé");
        }
        
    }
}


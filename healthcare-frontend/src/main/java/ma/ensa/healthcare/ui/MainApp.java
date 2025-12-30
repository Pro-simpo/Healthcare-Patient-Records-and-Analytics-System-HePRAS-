package ma.ensa.healthcare.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ma.ensa.healthcare.config.HikariCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Point d'entrée de l'application JavaFX Healthcare System
 */
public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;

            // Charger l'icône (PNG, ICO, JPG…)
            Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));

            // Appliquer l'icône au Stage
            stage.getIcons().add(icon);

            // Configuration de la fenêtre principale
            primaryStage.setTitle("Healthcare System - Connexion");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.setResizable(false);
            
            // Icône de l'application (optionnel)
            // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            
            // Charger l'écran de connexion
            showLoginScreen();
            
            primaryStage.show();
            logger.info("Application JavaFX démarrée avec succès");
            
        } catch (Exception e) {
            logger.error("Erreur lors du démarrage de l'application", e);
            e.printStackTrace();
        }
    }

    /**
     * Affiche l'écran de connexion
     */
    public static void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Healthcare System - Connexion");
            primaryStage.setMaximized(true);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'écran de connexion", e);
            e.printStackTrace();
        }
    }

    /**
     * Affiche le dashboard principal après connexion
     */
    public static void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Healthcare System - Dashboard");
            primaryStage.setMaximized(true);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du dashboard", e);
            e.printStackTrace();
        }
    }

    /**
     * Retourne le Stage principal pour les dialogues
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        // Fermeture propre du pool de connexions
        logger.info("Fermeture de l'application...");
        HikariCPConfig.getDataSource().close();
        logger.info("Pool de connexions fermé");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
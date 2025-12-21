package ma.ensa.healthcare.service;

public class NotificationService {
    public void envoyerRappelRendezVous(String email, String date) {
        System.out.println("Envoi d'un mail à " + email + " pour le rdv du " + date);
    }
}
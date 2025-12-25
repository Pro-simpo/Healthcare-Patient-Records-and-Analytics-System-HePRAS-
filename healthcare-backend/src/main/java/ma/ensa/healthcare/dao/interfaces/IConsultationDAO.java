package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.Consultation;
import java.util.List;

public interface IConsultationDAO {
    Consultation save(Consultation consultation);
    Consultation findById(Long id);
    List<Consultation> findByPatientId(Long patientId);
    List<Consultation> findByMedecinId(Long medecinId);
    List<Consultation> findAll();
    void update(Consultation consultation);
    void delete(Long id);
    Consultation findByRendezVousId(Long rdvId);
}
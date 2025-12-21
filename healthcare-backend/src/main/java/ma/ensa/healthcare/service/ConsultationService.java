package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.ConsultationDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IConsultationDAO;
import ma.ensa.healthcare.model.Consultation;
import java.util.List;

public class ConsultationService {
    private final IConsultationDAO consultationDAO = new ConsultationDAOImpl();

    public Consultation enregistrerConsultation(Consultation c) {
        // Logique métier : une consultation doit avoir un diagnostic
        if(c.getDiagnostic() == null || c.getDiagnostic().isEmpty()) {
            throw new RuntimeException("Le diagnostic est obligatoire");
        }
        return consultationDAO.save(c);
    }

    public List<Consultation> listerToutesConsultations() {
        return consultationDAO.findAll();
    }
}
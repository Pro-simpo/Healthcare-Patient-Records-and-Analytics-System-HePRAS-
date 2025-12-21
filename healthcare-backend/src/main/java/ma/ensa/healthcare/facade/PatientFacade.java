package ma.ensa.healthcare.facade;

import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.service.PatientService;
import java.util.List;

public class PatientFacade {
    
    private final PatientService patientService;

    public PatientFacade() {
        this.patientService = new PatientService();
    }

    public Patient inscrirePatient(Patient p) {
        return patientService.createPatient(p);
    }

    public List<Patient> listerPatients() {
        return patientService.getAllPatients();
    }
}
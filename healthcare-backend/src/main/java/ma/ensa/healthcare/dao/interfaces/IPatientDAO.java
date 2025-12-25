package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.Patient;
import java.util.List;

public interface IPatientDAO {
    Patient save(Patient patient);
    Patient findById(Long id);
    Patient findByCin(String cin);
    List<Patient> findAll();
    void update(Patient patient);
    void delete(Long id);
    List<Patient> findByNom(String nom);
}
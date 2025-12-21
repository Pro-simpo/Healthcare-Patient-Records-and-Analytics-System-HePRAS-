package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.PatientDAOImpl;
import ma.ensa.healthcare.dto.StatisticsDTO;

public class AnalyticsService {
    public StatisticsDTO getGlobalStats() {
        StatisticsDTO stats = new StatisticsDTO();
        // Ici, on appellerait des méthodes de comptage SQL
        // stats.setTotalPatients(patientDAO.count());
        return stats;
    }
}
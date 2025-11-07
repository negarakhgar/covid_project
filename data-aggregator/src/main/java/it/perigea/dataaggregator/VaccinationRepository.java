package it.perigea.dataaggregator;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VaccinationRepository extends MongoRepository<VaccinationDoc, String> {}

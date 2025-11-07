package it.perigea.dataaggregator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class VaccinationConsumer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final VaccinationRepository repo;

    public VaccinationConsumer(VaccinationRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "SOMMINISTRAZIONI-VACCINI-COVID19-LOMBARDIA", groupId = "vaccini-group")
    public void consume(String json) throws Exception {
        VaccinationDoc doc = mapper.readValue(json, VaccinationDoc.class);
        repo.save(doc);
    }
}


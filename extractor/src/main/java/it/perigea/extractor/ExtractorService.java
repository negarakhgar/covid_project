package it.perigea.extractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExtractorService {
    private static final String URL =
            "https://hub.dati.lombardia.it/resource/vtzi-zmp8.json";
    private static final String TOPIC =
            "SOMMINISTRAZIONI-VACCINI-COVID19-LOMBARDIA";

    private final ProvinceRepository provinces;
    private final KafkaTemplate<String, String> kafka;
    private final WebClient web = WebClient.create();
    private final ObjectMapper mapper = new ObjectMapper();

    public ExtractorService(ProvinceRepository provinces,
                            KafkaTemplate<String, String> kafka) {
        this.provinces = provinces;
        this.kafka = kafka;
    }

    public String runOnce() throws Exception {
        VaccinationRow[] rows = web.get()
                .uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(VaccinationRow[].class)
                .block();

        if (rows == null || rows.length == 0) return "No data received.";

        String ts = Instant.now().toString();
        int sent = 0;

        for (VaccinationRow r : rows) {
            String provName = r.provincia_dom() == null ? "" : r.provincia_dom().toUpperCase();
            String provCode = provinces.findCodeByName(provName);

            Map<String, Object> doc = new HashMap<>();
            doc.put("timestamp", ts);
            doc.put("codistat_comune_dom", r.codistat_comune_dom());
            doc.put("comune_dom", r.comune_dom());
            doc.put("provincia_dom", r.provincia_dom());
            doc.put("provincia_codice", provCode);
            doc.put("tot_solo_dose_1", parseNum(r.tot_solo_dose_1()));
            doc.put("tot_dose_2_unica", parseNum(r.tot_dose_2_unica()));
            doc.put("tot_dose_addizionale_booster", parseNum(r.tot_dose_addizionale_booster()));
            doc.put("totdose_richimm_rich2", parseNum(r.totdose_richimm_rich2()));

            String json = mapper.writeValueAsString(doc);
            kafka.send(TOPIC, json);
            sent++;
        }
        return "Sent " + sent + " messages to Kafka.";
    }

    private long parseNum(String s) {
        try { return (s == null || s.isBlank()) ? 0L : Long.parseLong(s); }
        catch (NumberFormatException e) { return 0L; }
    }
}


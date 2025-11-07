package it.perigea.dataaggregator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("vaccinations")
public class VaccinationDoc {
    @Id
    private String id;

    private String timestamp;
    private String codistat_comune_dom;
    private String comune_dom;

    @Indexed
    private String provincia_dom;

    @Indexed
    private String provincia_codice;

    private long tot_solo_dose_1;
    private long tot_dose_2_unica;
    private long tot_dose_addizionale_booster;
    private long totdose_richimm_rich2;

    public String getId() { return id; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getCodistat_comune_dom() { return codistat_comune_dom; }
    public void setCodistat_comune_dom(String s) { this.codistat_comune_dom = s; }
    public String getComune_dom() { return comune_dom; }
    public void setComune_dom(String s) { this.comune_dom = s; }
    public String getProvincia_dom() { return provincia_dom; }
    public void setProvincia_dom(String s) { this.provincia_dom = s; }
    public String getProvincia_codice() { return provincia_codice; }
    public void setProvincia_codice(String s) { this.provincia_codice = s; }
    public long getTot_solo_dose_1() { return tot_solo_dose_1; }
    public void setTot_solo_dose_1(long v) { this.tot_solo_dose_1 = v; }
    public long getTot_dose_2_unica() { return tot_dose_2_unica; }
    public void setTot_dose_2_unica(long v) { this.tot_dose_2_unica = v; }
    public long getTot_dose_addizionale_booster() { return tot_dose_addizionale_booster; }
    public void setTot_dose_addizionale_booster(long v) { this.tot_dose_addizionale_booster = v; }
    public long getTotdose_richimm_rich2() { return totdose_richimm_rich2; }
    public void setTotdose_richimm_rich2(long v) { this.totdose_richimm_rich2 = v; }
}


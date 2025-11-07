package it.perigea.dataaggregator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AggregationController {

    private final MongoTemplate mongo;

    public AggregationController(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    // total single-dose
    @GetMapping("/total/single-dose")
    public Map<String, Object> totalSingleDose() {
        var agg = Aggregation.newAggregation(
                Aggregation.group().sum("tot_solo_dose_1").as("total")
        );
        AggregationResults<Map> res = mongo.aggregate(agg, "vaccinations", Map.class);
        return res.getUniqueMappedResult();
    }

    // total double-dose
    @GetMapping("/total/double-dose")
    public Map<String, Object> totalDoubleDose() {
        var agg = Aggregation.newAggregation(
                Aggregation.group().sum("tot_dose_2_unica").as("total")
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getUniqueMappedResult();
    }

    // total single-dose for a given province (name in uppercase, e.g., MILANO)
    @GetMapping("/province/{name}/single-dose")
    public Map<String, Object> singleDoseByProvince(@PathVariable String name) {
        var agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("provincia_dom").is(name.toUpperCase())),
                Aggregation.group().sum("tot_solo_dose_1").as("total")
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getUniqueMappedResult();
    }

    // total double-dose for a given province
    @GetMapping("/province/{name}/double-dose")
    public Map<String, Object> doubleDoseByProvince(@PathVariable String name) {
        var agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("provincia_dom").is(name.toUpperCase())),
                Aggregation.group().sum("tot_dose_2_unica").as("total")
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getUniqueMappedResult();
    }

    //vaccination numbers grouped by province
    @GetMapping("/group-by-province")
    public List<Map> groupByProvince() {
        var agg = Aggregation.newAggregation(
                Aggregation.group("provincia_dom")
                        .sum("tot_solo_dose_1").as("singleDose")
                        .sum("tot_dose_2_unica").as("doubleDose")
                        .sum("tot_dose_addizionale_booster").as("booster")
                        .sum("totdose_richimm_rich2").as("recall"),
                Aggregation.sort(org.springframework.data.domain.Sort.by("singleDose").descending())
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getMappedResults();
    }

    //vaccination numbers grouped by municipality, for a given province
    @GetMapping("/province/{name}/group-by-municipality")
    public List<Map> groupByMunicipality(@PathVariable String name) {
        var agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("provincia_dom").is(name.toUpperCase())),
                Aggregation.group("comune_dom")
                        .sum("tot_solo_dose_1").as("singleDose")
                        .sum("tot_dose_2_unica").as("doubleDose"),
                Aggregation.sort(org.springframework.data.domain.Sort.by("singleDose").descending())
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getMappedResults();
    }

    //municipality with highest/lowest single or double dose (global)
    @GetMapping("/max/single-dose")
    public Map<String, Object> maxSingleDoseMunicipality() {
        var agg = Aggregation.newAggregation(
                Aggregation.group("comune_dom").sum("tot_solo_dose_1").as("total"),
                Aggregation.sort(org.springframework.data.domain.Sort.by("total").descending()),
                Aggregation.limit(1)
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getUniqueMappedResult();
    }

    @GetMapping("/max/double-dose")
    public Map<String, Object> maxDoubleDoseMunicipality() {
        var agg = Aggregation.newAggregation(
                Aggregation.group("comune_dom").sum("tot_dose_2_unica").as("total"),
                Aggregation.sort(org.springframework.data.domain.Sort.by("total").descending()),
                Aggregation.limit(1)
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getUniqueMappedResult();
    }

    @GetMapping("/min/single-dose")
    public Map<String, Object> minSingleDoseMunicipality() {
        var agg = Aggregation.newAggregation(
                Aggregation.group("comune_dom").sum("tot_solo_dose_1").as("total"),
                Aggregation.sort(org.springframework.data.domain.Sort.by("total").ascending()),
                Aggregation.limit(1)
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getUniqueMappedResult();
    }

    @GetMapping("/min/double-dose")
    public Map<String, Object> minDoubleDoseMunicipality() {
        var agg = Aggregation.newAggregation(
                Aggregation.group("comune_dom").sum("tot_dose_2_unica").as("total"),
                Aggregation.sort(org.springframework.data.domain.Sort.by("total").ascending()),
                Aggregation.limit(1)
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getUniqueMappedResult();
    }

    //same “max” but filtered by province
    @GetMapping("/province/{name}/max/single-dose")
    public Map<String, Object> maxSingleDoseByProvince(@PathVariable String name) {
        var agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("provincia_dom").is(name.toUpperCase())),
                Aggregation.group("comune_dom").sum("tot_solo_dose_1").as("total"),
                Aggregation.sort(org.springframework.data.domain.Sort.by("total").descending()),
                Aggregation.limit(1)
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getUniqueMappedResult();
    }

    @GetMapping("/province/{name}/max/double-dose")
    public Map<String, Object> maxDoubleDoseByProvince(@PathVariable String name) {
        var agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("provincia_dom").is(name.toUpperCase())),
                Aggregation.group("comune_dom").sum("tot_dose_2_unica").as("total"),
                Aggregation.sort(org.springframework.data.domain.Sort.by("total").descending()),
                Aggregation.limit(1)
        );
        return mongo.aggregate(agg, "vaccinations", Map.class).getUniqueMappedResult();
    }
}


package it.perigea.extractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProvinceRepository {
    private final JdbcTemplate jdbc;

    public ProvinceRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public String findCodeByName(String nameUpper) {
        return jdbc.query(
                "SELECT codice FROM province_ita WHERE UPPER(nome) = ?",
                ps -> ps.setString(1, nameUpper),
                rs -> rs.next() ? rs.getString("codice") : null
        );
    }
}


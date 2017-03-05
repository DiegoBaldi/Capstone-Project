package nanodegree.diegobaldi.it.tonightmovie.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by diego on 25/02/2017.
 */

public class ProductionCountry {
    private String iso31661;
    private String name;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getIso31661() {
        return iso31661;
    }

    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

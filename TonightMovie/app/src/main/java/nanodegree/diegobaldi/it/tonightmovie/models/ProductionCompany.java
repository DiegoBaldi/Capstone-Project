package nanodegree.diegobaldi.it.tonightmovie.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by diego on 25/02/2017.
 */

public class ProductionCompany {
    private String name;
    private int id;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

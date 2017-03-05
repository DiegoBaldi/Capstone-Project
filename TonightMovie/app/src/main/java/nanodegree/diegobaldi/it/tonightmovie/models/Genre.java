package nanodegree.diegobaldi.it.tonightmovie.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by diego on 25/02/2017.
 */

public class Genre {
    private int id;
    private String name;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

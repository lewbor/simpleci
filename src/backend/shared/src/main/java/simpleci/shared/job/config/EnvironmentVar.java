package simpleci.shared.job.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentVar {
    public final String name;
    public final String value;

    public EnvironmentVar(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public EnvironmentVar(JsonObject json) {
        name = json.get("name").getAsString();
        value = json.get("value").getAsString();
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("value", value);
        return json;
    }

    public String toBashString() {
        return name + "=" + value;
    }



    public static List<String> envListToStringList(List<EnvironmentVar> envVars) {
        List<String> envStringList = new ArrayList<>(envVars.size());
        for(EnvironmentVar envVar : envVars) {
            envStringList.add(envVar.toBashString());
        }
        return envStringList;
    }



}

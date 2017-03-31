package simpleci.shared;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;
import java.util.function.Function;

public final class JsonUtils {

    public static String date(Date date) {
        return String.valueOf(date.getTime());
    }

    public static Date tsToDate(String timestamp) {
        return new Date(Long.valueOf(timestamp));
    }

    public static List<Integer> jsonArrayToIntList(JsonArray array) {
        List<Integer> list = new ArrayList<>(array.size());
        for(JsonElement element : array) {
            list.add(element.getAsInt());
        }
        return list;
    }

    public static Set<Integer> jsonArrayToIntSet(JsonArray array) {
        Set<Integer> set = new HashSet<>(array.size());
        for(JsonElement element : array) {
            set.add(element.getAsInt());
        }
        return set;
    }

    public static Set<Long> jsonArrayToLongSet(JsonArray array) {
        Set<Long> set = new HashSet<>(array.size());
        for(JsonElement element : array) {
            set.add(element.getAsLong());
        }
        return set;
    }

    public static List<String> jsonArrayToStringList(JsonArray array) {
        List<String> list = new ArrayList<>(array.size());
        for(JsonElement element : array) {
            list.add(element.getAsString());
        }
        return list;
    }

    public static List<String> jsonToListOfString(JsonArray array) {
        List<String> list = new ArrayList<>(array.size());
        for(JsonElement element : array) {
            list.add(element.getAsString());
        }
        return list;
    }

    public static JsonElement deepClone(JsonElement element) {
        try {
            String elementString = element.toString();
            return new JsonParser().parse(elementString);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }

    public static JsonElement setOfInt(Set<Integer> list) {
        JsonArray array = new JsonArray();
        for(Integer element : list){
            array.add(element);
        }
        return array;
    }

    public static JsonElement setOfLong(Set<Long> list) {
        JsonArray array = new JsonArray();
        for(Long element : list){
            array.add(element);
        }
        return array;
    }

    public static JsonArray listOfString(List<String> list) {
        JsonArray array = new JsonArray();
        for(String element : list) {
            array.add(element);
        }
        return array;
    }

    public static <T> JsonArray listOfObjects(List<T> list, Function<T, JsonElement> converter) {
        JsonArray array = new JsonArray();
        for(T element : list) {
            array.add(converter.apply(element));
        }
        return array;
    }

    public static <T> List<T> jsonToList(JsonArray array, Function<JsonElement, T> converter) {
        List<T> list = new ArrayList<>(array.size());
        for(JsonElement element : array) {
            list.add(converter.apply(element));
        }
        return list;
    }


}

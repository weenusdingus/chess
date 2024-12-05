package JsonConverter;

import com.google.gson.Gson;

import java.io.InputStreamReader;

public class JsonConverter {
  private static JsonConverter instance;
  private Gson serializer;

  private JsonConverter() {
    serializer = new Gson();
  }

  public static JsonConverter getInstance() {
    if(instance == null) {
      instance = new JsonConverter();
    }
    return instance;
  }

  public <T> String toJson(T object) {
    return serializer.toJson(object);
  }

  public <T> T fromJson(String json, Class<T>  type) {
    return serializer.fromJson(json, type);
  }

  public <T> T fromJson(InputStreamReader reader, Class<T>  type) {
    return serializer.fromJson(reader, type);
  }
}
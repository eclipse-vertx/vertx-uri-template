package io.vertx.uritemplate;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Converter and mapper for {@link io.vertx.uritemplate.ExpandOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.uritemplate.ExpandOptions} original class using Vert.x codegen.
 */
public class ExpandOptionsConverter {


  private static final Base64.Decoder BASE64_DECODER = JsonUtil.BASE64_DECODER;
  private static final Base64.Encoder BASE64_ENCODER = JsonUtil.BASE64_ENCODER;

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, ExpandOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "allowVariableMiss":
          if (member.getValue() instanceof Boolean) {
            obj.setAllowVariableMiss((Boolean)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(ExpandOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(ExpandOptions obj, java.util.Map<String, Object> json) {
    json.put("allowVariableMiss", obj.getAllowVariableMiss());
  }
}

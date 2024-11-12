package io.vertx.uritemplate;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter and mapper for {@link io.vertx.uritemplate.ExpandOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.uritemplate.ExpandOptions} original class using Vert.x codegen.
 */
public class ExpandOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, ExpandOptions obj) {
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

   static void toJson(ExpandOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(ExpandOptions obj, java.util.Map<String, Object> json) {
    json.put("allowVariableMiss", obj.getAllowVariableMiss());
  }
}

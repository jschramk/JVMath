package core.old;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.components.Operand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

@Deprecated
public class RuleWriter {

  public static void main(String[] args) throws IOException {

    File simplifyFile = new File("src/core/rewrite/simplify.json");
    File solveFile = new File("src/core/rewrite/solve.json");


    //runWriteDriver(simplify, simplifyFile);
    //runWriteDriver(solve, solveFile);

  }

  private static JsonObject rule(String find, String replace, JsonObject... requirements) {
    JsonObject result = new JsonObject();
    result.addProperty("find", find);
    result.addProperty("replace", replace);
    if (requirements.length > 0) {
      JsonArray array = new JsonArray();
      result.add("require", array);
      for (JsonObject object : requirements) {
        array.add(object);
      }
    }
    return result;
  }

  private static JsonArray array(JsonObject... objects) {
    JsonArray array = new JsonArray();
    for (JsonObject object : objects) {
      array.add(object);
    }
    return array;
  }

  private static void writeRules(JsonArray jsonArray, File file) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    String out = gson.toJson(jsonArray);
    FileWriter writer = new FileWriter(file);
    writer.write(out);
    writer.close();
  }

  private static void runWriteDriver(JsonArray jsonArray, File file) throws IOException {

    if (!file.createNewFile()) {

      System.out.print("Would you like to overwrite the contents of the following file?\n\n" + file
          .getCanonicalPath() + "\n\n(y/n): ");
      Scanner s = new Scanner(System.in);

      while (s.hasNextLine()) {

        String response = s.nextLine().trim().toLowerCase();

        if (response.equals("y")) {
          break;
        } else if (response.equals("n")) {
          System.out.println("Write aborted");
          System.exit(1);
        } else {
          System.out.print("Please answer y for yes or n for no: ");
        }

      }

    }

    writeRules(jsonArray, file);

    System.out.println("Write completed");


  }


}

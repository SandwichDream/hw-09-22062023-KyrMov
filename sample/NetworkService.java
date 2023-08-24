package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class NetworkService {

  public static String getStringFromURL(String spec, String code) throws IOException {
    URL url = new URL(spec);
    URLConnection connection = url.openConnection();
    String result = "";
    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), code))) {
      String temp = "";
      for (;;) {
        temp = br.readLine();
        if (temp == null) {
          break;
        }
        result += temp + System.lineSeparator();
      }
    }
    return result;
  }

  public static long getFileFromURL(String spec, File folder) throws IOException {
    URL url = new URL(spec);
    URLConnection connection = url.openConnection();
    int n = spec.lastIndexOf("/");
    String fileName = spec.substring(n + 1);
    File file = new File(folder, fileName);

    try (InputStream is = connection.getInputStream(); OutputStream os = new FileOutputStream(file)) {
      return is.transferTo(os);
    }
  }

  public static Map<String, List<String>> getHeaderFromURL(String spec) throws IOException {
    URL url = new URL(spec);
    URLConnection connection = url.openConnection();
    return connection.getHeaderFields();
  }

  public static void linksToFile(String htmlText) throws IOException {
    StringBuilder result = new StringBuilder();
    int startIndex = 0;

    while (true) {
      int linkStartHttp = htmlText.indexOf("http://", startIndex);
      int linkStartHttps = htmlText.indexOf("https://", startIndex);

      if (linkStartHttp == -1 && linkStartHttps == -1) {
        break;
      }

      int linkStart;
      if (linkStartHttp == -1) {
        linkStart = linkStartHttps;
      } else if (linkStartHttps == -1) {
        linkStart = linkStartHttp;
      } else {
        linkStart = Math.min(linkStartHttp, linkStartHttps);
      }

      int linkEnd = htmlText.indexOf("\"", linkStart);
      if (linkEnd == -1) {
        break;
      }

      String link = htmlText.substring(linkStart, linkEnd);
      result.append(link).append(System.lineSeparator());

      startIndex = linkEnd + 1;
    }

    try (PrintWriter pw = new PrintWriter("folder/Links.txt")) {
      pw.write(result.toString());
      System.out.println("Файл створений folder/Links.txt");
    } catch (IOException e) {
      System.out.println("Косяк: " + e.getMessage());
    }
  }

  public static void checkingLinksForValidity(String path) {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line;
      while ((line = br.readLine()) != null) {
        URL url = new URL(line);
        try {
          HttpURLConnection connection = (HttpURLConnection) url.openConnection();

          if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println(line + " =========> Валідний");
          } else {
            System.out.println(line + " =========> ІНВАЛІДНИЙ!!!");
          }
        } catch (IOException e) {
          System.out.println("Косяк: " + e.getMessage());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
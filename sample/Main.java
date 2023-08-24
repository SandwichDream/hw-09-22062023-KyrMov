package sample;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String spec = "https://dou.ua/";
        String htmlText = "";
        try {
            htmlText = NetworkService.getStringFromURL(spec, "UTF-8");
            NetworkService.linksToFile(htmlText);
        } catch (IOException e) {
            e.printStackTrace();
        }

        NetworkService.checkingLinksForValidity("folder/Links.txt");

    }
}
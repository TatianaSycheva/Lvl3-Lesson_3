package com.example.client_chat.history;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HistoryService {
    public static String path = "src/main/java/com/example/client_chat/history";


    public static String loadHistory(String nickname) {

        StringBuilder sb = new StringBuilder();
        int maxLines = 100;
        String fileName = String.format("/history_%s.txt", nickname);

        List<String> result = new ArrayList<>();

        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(path, fileName), StandardCharsets.UTF_8)) {

            String line;
            while ((line = reader.readLine()) != null && result.size() < maxLines) {
                result.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String str : result) {
            sb.insert(0, maxLines-- + ". " + str + "\n");
        }
        return sb.toString();
    }


    public static void saveHistory(String nickname, String text) {
        String fileName = String.format(path + "/history_%s.txt", nickname);
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        File history = new File(fileName);
        if (!history.exists()) {
            try {
                history.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileWriter = new FileWriter(fileName, true);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(text);
            bufferedWriter.flush();

            bufferedWriter.close();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package ru.deverty.word_learn;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static final String wordsPath = "words.txt";
    static final String settingsPath = "settings.txt";

    static final String SPLITTER = "--------";
    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_GREEN = "\u001B[32m";

    static Map<String, String> allWords = new HashMap<>();
    static boolean rightToLeft = false;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        LoadData();
        GameManager();
    }

    private static void GameManager()
    {
        if (rightToLeft)
            allWords = allWords.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        ArrayList<String> leftWords = new ArrayList<>(allWords.keySet());
        Collections.shuffle(leftWords);
        boolean flag = false;
        while (!flag)
        {
            Collections.shuffle(leftWords);
            int wordCount = Integer.MAX_VALUE;
            String readValue;
            while (!flag && (wordCount > allWords.size() || wordCount <= 0))
            {
                Print("Напишите количество слов (1 - " + allWords.size() + ')' + " (f - для выхода)");
                readValue = scanner.nextLine();
                if (readValue.equals("f"))
                    flag = true;
                else if (IsNumeric(readValue))
                {
                    wordCount = Integer.parseInt(readValue);
                    Print(SPLITTER);
                }

            }
            if (!flag)
                Game(leftWords.subList(0, wordCount).toArray(new String[0]));
        }
    }

    private static void Game(String[] words)
    {
        int correct = 0;
        String answer;
        for (String word : words)
        {
            System.out.print(word + " : ");
            answer = scanner.nextLine().toLowerCase().trim();
            if (answer.equals(allWords.get(word)))
            {
                Print(ANSI_GREEN + "Правильно!" + ANSI_RESET);
                correct++;
            }
            else
                Print(ANSI_RED + allWords.get(word).toUpperCase() + ANSI_RESET);
            Print(SPLITTER);
        }
        Print("(" + correct + " / " + words.length + ")\n");
        scanner.nextLine();
    }

    private static void Print(String caption)
    {
        System.out.println(caption);
    }

    public static boolean IsNumeric(String str) {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }

    private static void CreateFile(String path)
    {
        File file = new File(path);
        try
        {
            if (!file.createNewFile())
                Print("Не удалось создать файл " + path);
        }
        catch (IOException ioException)
        {
            System.out.println("IOException");
        }
    }

    private static String[] ReadFile(String path)
    {
        ArrayList<String> fileData = new ArrayList<>();
        try
        {
            BufferedReader fileScanner = new BufferedReader(
                    new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String strValue;
            while ((strValue = fileScanner.readLine())!= null)
                fileData.add(strValue);
            fileScanner.close();
        }
        catch (Exception e)
        {
            CreateFile(path);
        }
        return fileData.toArray(new String[0]);
    }

    private static boolean IntToBoolean(int value)
    {
        return value == 1;
    }

    private static void LoadData()
    {
        String[] wordsData = ReadFile(wordsPath);
        if (wordsData.length == 0)
        {
            Print("Слова не найдены");
            scanner.nextLine();
            System.exit(0);
        }
        for (String value : wordsData)
        {
            String[] words2 = value.split(";");
            allWords.put(words2[0].toLowerCase().trim(), words2[1].toLowerCase().trim());
        }
        String[] settingsData = ReadFile(settingsPath);
        if (settingsData.length == 0)
        {
            try
            {
                FileWriter myWriter = new FileWriter(settingsPath);
                myWriter.write("right_to_left=0");
                myWriter.close();
                settingsData = ReadFile(settingsPath);
            }
            catch (IOException ioException)
            {
                System.out.println("IOException");
            }
        }
        Map<String, Boolean> settings = new HashMap<>();
        for(String value: settingsData)
        {
            String[] set = value.split("=");
            settings.put(set[0], IntToBoolean(Integer.parseInt(set[1])));
        }
        if (settings.get("right_to_left"))
            rightToLeft = true;
    }
}
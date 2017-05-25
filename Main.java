import java.io.*;

/**
 * Based on using symbols' ASCII codes in decimal numeral system.
 * A-Z - 65-90
 * a-z - 97-122
 * А-Я - 1040-1071 (Ё - 1025)
 * а-я - 1072-1193 (ё - 1105)
 **/

public class Main {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static File file;
    private static String METHOD;
    private static int KEY;
    private static String KEYWORD;
    private static String PROCEDURETYPE;
    private static String FILEPATH;
    private static String INPUTWORD;
    private static String UTF8_BOM = "\uFEFF";
    private static String USAGE = "Usage:" + "\n" + "type separated by spaces options according to pattern below" + "\n" +
            "Method{Caesar/Vigenere} ProcedureType{Encrypt/Decrypt} Key{Keyword/[1-32]} {Encrypted/original text/filepath}" + "\n" +
            "or type \"Build Vigenere tabel\" and press \"Enter\"";

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(USAGE);
        String response = "";
        String input;
        while ((input = reader.readLine()) != null) {
            response = checkCommand(input.split(" "));

            if (input.equalsIgnoreCase("Build Vigenere tabel")) {
                buildTabulaRecta();
                System.out.println("Vigenere tabel was built successfully. It placed in " + FILEPATH);
                System.exit(0);
            } else if (response.equals("OK")) {
                break;
            } else {
                System.out.println(response);
                Thread.currentThread().sleep(2000);
                System.out.println(USAGE);
            }
        }

        if (response.equals("OK")) {
            if (FILEPATH != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FILEPATH), "Windows-1251"));
                if (METHOD.equalsIgnoreCase("Caesar")) {
                    while ((INPUTWORD = in.readLine()) != null) {
                        removeUTF8BOM(INPUTWORD);
                        System.out.println(makeConvertation(PROCEDURETYPE, INPUTWORD, KEY));
                    }
                } else if (METHOD.equalsIgnoreCase("Vigenere")) {
                    while ((INPUTWORD = in.readLine()) != null) {
                        removeUTF8BOM(INPUTWORD);
                        System.out.println(makeConvertation(PROCEDURETYPE, INPUTWORD, KEYWORD));
                    }
                }

            } else if (INPUTWORD != null) {
                if (METHOD.equalsIgnoreCase("Caesar")) {
                    removeUTF8BOM(INPUTWORD);
                    System.out.println(makeConvertation(PROCEDURETYPE, INPUTWORD, KEY));
                } else if (METHOD.equalsIgnoreCase("Vigenere")) {
                    removeUTF8BOM(INPUTWORD);
                    System.out.println(makeConvertation(PROCEDURETYPE, INPUTWORD, KEYWORD));
                }
            }
        } else if (response.equals("ERR")) {
            System.out.println("Some errors happened. Program will exit.");
            System.exit(0);
        }
    }

    private static String checkCommand(String[] input) throws IOException {
        if (input.length < 4) return "Incorrect request. Your number of arguments: " + input.length + ". Required: 4.";

        boolean statusOkMethod = false;
        boolean statusOkProcedure = false;
        boolean statusOkKey = false;
        boolean statusOkData = false;

        String method = input[0];
        String procedureType = input[1];
        String key = input[2];
        String textOrFilepath = "";
        for (int i = 3; i < input.length; i++) textOrFilepath += input[i] + " ";

        // method
        if (!method.equalsIgnoreCase("Caesar") && !method.equalsIgnoreCase("Vigenere")) {
            return "Please, choose method correctly. No such method: " + method;
        } else {
            METHOD = method;
            statusOkMethod = true;
        }

        // procedure type
        if (procedureType.equalsIgnoreCase("encrypt")) {
            PROCEDURETYPE = procedureType;
            statusOkProcedure = true;
        } else if (procedureType.equalsIgnoreCase("decrypt")) {
            PROCEDURETYPE = procedureType;
            statusOkProcedure = true;
        } else {
            return "Please, choose correct procedure type. No such type: " + procedureType;
        }

        // keys checking
        if (method.equalsIgnoreCase("Caesar")) {
            if (key.matches("^-[0-9]+$")) {
                return "Please, choose correct key. Key must not contain negative numbers: " + key;
            } else if (key.matches("^[a-zA-Zа-яА-Я]+$")) {
                return "Please, choose correct key. Key for Caesar method must not contain characters: " + key;
            } else {
                KEY = Integer.parseInt(key);
                statusOkKey = true;
            }
        }

        if (method.equalsIgnoreCase("Vigenere")) {
            if (key.matches("^(?=[^\\s]*?[0-9])(?=[^\\s]*?[a-zA-Zа-яА-Я])[a-zA-Zа-яА-Я0-9]*$")) {
                return "Please, choose correct key. Key must not contain combination of symbols and digits: " + key;
            } else if (key.matches("^-?[0-9]+$")) {
                return "Please, choose correct key. Key for Vinegere method must not contain any numbers: " + key;
            } else {
                KEYWORD = key;
                statusOkKey = true;
            }
        }

        // checking data types to handle
        if (textOrFilepath.matches("^[A-Z]+:\\\\+.+$")) {
            file = new File(textOrFilepath);
            if (file.exists()) {
                statusOkData = true;
                FILEPATH = file.getAbsolutePath();
            } else {
                return "Please, specify filepath correctly. No such filepath: " + textOrFilepath;
            }
        } else {
            if (textOrFilepath.matches("^[0-9$&+,:;=?@#|'<>.^*()%!_-]+$")) {
                System.out.println("WARNING: in proccess of encryption/decryption any special characters and numbers will be ignored!");
            }
            INPUTWORD = textOrFilepath;
            statusOkData = true;
        }


        if (statusOkMethod && statusOkKey && statusOkData && statusOkProcedure) {
            return "OK";
        } else return "ERR";
    }

    private static void buildTabulaRecta() throws IOException {
        FILEPATH = "D:\\tabula recta.txt";
        File file = new File(FILEPATH);
        PrintWriter writer = new PrintWriter(file.getAbsoluteFile());

        int start = 65;
        int end = 90;
        int currentLineStart;

        for (int i = 0; i < 26; i++) {
            currentLineStart = start + i;
            for (int j = 0; j < 26; j++) {
                int displacement = j;
                if (currentLineStart + displacement > end) {
                    writer.print(Character.toString((char)(currentLineStart + displacement + start - end - 1)) + " ");
                } else {
                    writer.print(Character.toString((char)(currentLineStart + displacement)) + " ");
                }
            }
            writer.println();
        }
        writer.close();
    }

    // Caesar
    private static String makeConvertation(String procedureType, String inputData, int displacement) throws UnsupportedEncodingException {
        String result = "";

        int[] codesBefore = new int[inputData.length()];
        int[] codesAfter = new int[inputData.length()];

        for (int i = 0; i < inputData.length(); i++) codesBefore[i] = (int)inputData.toCharArray()[i];

        for (int i = 0; i < codesBefore.length; i++) {
            int currentCode = codesBefore[i];

            // symbols
            if ((currentCode > 31 && currentCode < 65) || (currentCode > 90 && currentCode < 96)) {
                codesAfter[i] = currentCode;

                // latin uppercase
            } else if (currentCode > 64 && currentCode < 91) {
                int leftBound = 65;
                int rightBound = 90;
                if (procedureType.equalsIgnoreCase("encrypt")) codesAfter[i] = encryptCode(leftBound, rightBound, currentCode, displacement);
                if (procedureType.equalsIgnoreCase("decrypt")) codesAfter[i] = decryptCode(leftBound, rightBound, currentCode, displacement);

                // latin lowercase
            } else if (currentCode > 96 && currentCode < 123) {
                int leftBound = 97;
                int rightBound = 122;
                if (procedureType.equalsIgnoreCase("encrypt")) codesAfter[i] = encryptCode(leftBound, rightBound, currentCode, displacement);
                if (procedureType.equalsIgnoreCase("decrypt")) codesAfter[i] = decryptCode(leftBound, rightBound, currentCode, displacement);

                // cyrillic uppercase, Ё = 1025
            } else if (currentCode > 1039 && currentCode < 1072) {
                int leftBound = 1040;
                int rightBound = 1071;
                if (procedureType.equalsIgnoreCase("encrypt")) codesAfter[i] = encryptCode(leftBound, rightBound, currentCode, displacement);
                if (procedureType.equalsIgnoreCase("decrypt")) codesAfter[i] = decryptCode(leftBound, rightBound, currentCode, displacement);

                // cyrillic lowercase, ё = 1105
            } else if (currentCode > 1071 && currentCode < 1104) {
                int leftBound = 1072;
                int rightBound = 1103;
                if (procedureType.equalsIgnoreCase("encrypt")) codesAfter[i] = encryptCode(leftBound, rightBound, currentCode, displacement);
                if (procedureType.equalsIgnoreCase("decrypt")) codesAfter[i] = decryptCode(leftBound, rightBound, currentCode, displacement);
            }
        }

        for (int i = 0; i < codesAfter.length; i++) result += Character.toString((char)codesAfter[i]);

        return result;
    }

    // Vigenere
    private static String makeConvertation(String procedureType, String inputData, String keyword) throws UnsupportedEncodingException {
        String result = "";

        int[] displacements = convertKeywordToDisplacements(keyword);
        int[] codesBefore = new int[inputData.length()];
        int[] codesAfter = new int[inputData.length()];

        int displacementsIndexCounter = 0;
        for (int i = 0; i < inputData.length(); i++) codesBefore[i] = (int)inputData.toCharArray()[i];

        for (int i = 0; i < codesBefore.length; i++) {
            int currentCode = codesBefore[i];

            // symbols
            if ((currentCode > 31 && currentCode < 65) || (currentCode > 90 && currentCode < 96)) {
                codesAfter[i] = currentCode;

                // latin uppercase
            } else if (currentCode > 64 && currentCode < 91) {
                int leftBound = 65;
                int rightBound = 90;
                if (procedureType.equalsIgnoreCase("encrypt")) codesAfter[i] = encryptCode(leftBound, rightBound, currentCode, displacements[displacementsIndexCounter]);
                if (procedureType.equalsIgnoreCase("decrypt")) codesAfter[i] = decryptCode(leftBound, rightBound, currentCode, displacements[displacementsIndexCounter]);
                displacementsIndexCounter++;
                if (displacementsIndexCounter == displacements.length) displacementsIndexCounter = 0;

                // latin lowercase
            } else if (currentCode > 96 && currentCode < 123) {
                int leftBound = 97;
                int rightBound = 122;
                if (procedureType.equalsIgnoreCase("encrypt")) codesAfter[i] = encryptCode(leftBound, rightBound, currentCode, displacements[displacementsIndexCounter]);
                if (procedureType.equalsIgnoreCase("decrypt")) codesAfter[i] = decryptCode(leftBound, rightBound, currentCode, displacements[displacementsIndexCounter]);
                displacementsIndexCounter++;
                if (displacementsIndexCounter == displacements.length) displacementsIndexCounter = 0;

                // cyrillic uppercase, Ё = 1025
            } else if (currentCode > 1039 && currentCode < 1072) {
                int leftBound = 1040;
                int rightBound = 1071;
                if (procedureType.equalsIgnoreCase("encrypt")) codesAfter[i] = encryptCode(leftBound, rightBound, currentCode, displacements[displacementsIndexCounter]);
                if (procedureType.equalsIgnoreCase("decrypt")) codesAfter[i] = decryptCode(leftBound, rightBound, currentCode, displacements[displacementsIndexCounter]);
                displacementsIndexCounter++;
                if (displacementsIndexCounter == displacements.length) displacementsIndexCounter = 0;

                // cyrillic lowercase, ё = 1105
            } else if (currentCode > 1071 && currentCode < 1104) {
                int leftBound = 1072;
                int rightBound = 1103;
                if (procedureType.equalsIgnoreCase("encrypt")) codesAfter[i] = encryptCode(leftBound, rightBound, currentCode, displacements[displacementsIndexCounter]);
                if (procedureType.equalsIgnoreCase("decrypt")) codesAfter[i] = decryptCode(leftBound, rightBound, currentCode, displacements[displacementsIndexCounter]);
                displacementsIndexCounter++;
                if (displacementsIndexCounter == displacements.length) displacementsIndexCounter = 0;
            }
        }

        for (int i = 0; i < codesAfter.length; i++) result += Character.toString((char)codesAfter[i]);

        return result;
    }

    // displacement to right
    private static int encryptCode(int leftBound, int rightBound, int currentCode, int displacement) {
        int result;
        if (currentCode + displacement > rightBound) {
            result = currentCode + displacement + leftBound - rightBound - 1;
        } else {
            result = currentCode + displacement;
        }
        return result;
    }

    // displacement to left
    private static int decryptCode(int leftBound, int rightBound, int currentCode, int displacement) {
        int result;
        if (currentCode - displacement < leftBound) {
            result = currentCode - displacement - leftBound + rightBound + 1;
        } else {
            result = currentCode - displacement;
        }
        return result;
    }

    // converting keyword to displacements sequence
    private static int[] convertKeywordToDisplacements(String keyword) {
        int[] displacements = new int[keyword.length()];
        int[] codes = new int[keyword.length()];
        for (int i = 0; i < codes.length; i++) codes[i] = (int) keyword.toCharArray()[i];

        for (int i = 0; i < codes.length; i++) {
            int currentCode = codes[i];

            if (currentCode > 64 && currentCode < 91) {
                displacements[i] = currentCode - 65;
            } else if (currentCode > 96 && currentCode < 123) {
                displacements[i] = currentCode - 97;
            } else if (currentCode > 1039 && currentCode < 1072) {
                displacements[i] = currentCode - 1040;
            } else if (currentCode > 1071 && currentCode < 1104) {
                displacements[i] = currentCode - 1072;
            }
        }
        return displacements;
    }

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }
}
package cn.thinkinginjava.data.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DdlUtil {

    public static String rewriteDdl(String originalDDL) {
        if (!StringUtils.hasLength(originalDDL)) {
            return originalDDL;
        }
        int createTableStart = originalDDL.indexOf("CREATE TABLE");
        int createTableEnd = originalDDL.indexOf("(", createTableStart);
        String sourceDdl = null;
        if (createTableStart != -1 && createTableEnd != -1) {
            int openParenthesisCount = 1;
            int i = createTableEnd + 1;
            while (i < originalDDL.length()) {
                char c = originalDDL.charAt(i);
                if (c == '(') {
                    openParenthesisCount++;
                } else if (c == ')') {
                    openParenthesisCount--;
                }
                if (openParenthesisCount == 0) {
                    sourceDdl = originalDDL.substring(createTableStart, i + 1);
                    System.out.println(sourceDdl);
                    break;
                }
                i++;
            }
        }
        if (StringUtils.hasLength(sourceDdl)) {
            String pattern = "CREATE TABLE \".+?\"\\.";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(sourceDdl);
            sourceDdl = matcher.replaceAll("CREATE TABLE ");
            sourceDdl = sourceDdl.replaceAll("NOT NULL ENABLE","NOT NULL");
            sourceDdl = sourceDdl.replaceAll("\"(.*?)\"", "$1");
        }
        return sourceDdl;
    }
}

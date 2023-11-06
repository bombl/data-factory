package cn.thinkinginjava.data.utils;

import cn.thinkinginjava.data.service.impl.DatasetManager;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomUtil {

    public static Object getRandomValue(String datasetId) {
        DatasetManager datasetManager = SpringContextUtil.getBean(DatasetManager.class);
        if (datasetManager == null) {
            return null;
        }
        List<Object> dataList = datasetManager.getDataList(datasetId);
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(dataList.size());
        return dataList.get(randomIndex);
    }

    public static String generateRandomValue(ColumnDefinition columnDefinition) {
        String dataType = columnDefinition.getColDataType().getDataType().toLowerCase();
        List<String> argumentsStringList = columnDefinition.getColDataType().getArgumentsStringList();
        int length = argumentsStringList != null ? Integer.parseInt(argumentsStringList.get(0)) : 15;
        length = Math.min(length, 5);
        if (argumentsStringList == null) {
            length = 1;
        }
        int scale = 0;
        if (argumentsStringList != null && argumentsStringList.size() > 1) {
            scale = Integer.parseInt(argumentsStringList.get(1));
        }
        if (dataType.contains("tinyint") || dataType.contains("smallint") || dataType.contains("mediumint")) {
            return String.valueOf(new Random().nextInt(9));
        } else if (dataType.contains("int") || dataType.contains("number") || dataType.contains("bigint") || dataType.contains("numeric")) {
            if (length > 0 && length <= 9) {
                int randomNum = new Random().nextInt((int) Math.pow(10, length));
                return String.valueOf(randomNum);
            } else if (length > 9) {
                long randomNum = (long) (new Random().nextDouble() * Math.pow(10, length));
                return String.valueOf(randomNum);
            }
        } else if (dataType.contains("varchar") || dataType.contains("char") || dataType.contains("text")) {
            return "'" + RandomUtil.generateRandomString(length) + "'";
        } else if (dataType.contains("decimal") || dataType.contains("float") || dataType.contains("double")) {
            return RandomUtil.generateDecimalValue(length, scale);
        } else if (dataType.contains("date") || dataType.contains("timestamp")) {
            return RandomUtil.generateRandomDate();
        }
        return "'" + RandomUtil.generateRandomString(length) + "'";
    }

    public static String generateDecimalValue(int precision, int scale) {
        Random random = new Random();
        BigDecimal min = BigDecimal.valueOf(Math.pow(10, -scale));
        BigDecimal max = BigDecimal.valueOf(Math.pow(10, precision - scale)).subtract(min);
        BigDecimal randomValue = min.add(max.multiply(BigDecimal.valueOf(random.nextDouble())));
        return randomValue.setScale(scale, RoundingMode.HALF_UP).toString();
    }

    public static String generateNumberValue(int precision, int scale) {
        Random random = new Random();
        BigDecimal min = BigDecimal.valueOf(Math.max(Math.pow(10, -scale), Integer.MIN_VALUE));
        BigDecimal max = BigDecimal.valueOf(Math.min(Math.pow(10, precision - scale), Integer.MAX_VALUE)).subtract(min);
        BigDecimal randomValue = min.add(max.multiply(BigDecimal.valueOf(random.nextDouble())));
        return randomValue.setScale(scale, RoundingMode.HALF_UP).toString();
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char randomChar = characters.charAt(new Random().nextInt(characters.length()));
            result.append(randomChar);
        }
        return result.toString();
    }

    public static String generateRandomDate() {
        Random random = new Random();
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        int day = random.nextInt(getDaysInMonth(currentYear, currentMonth)) + 1;
        int hour = random.nextInt(23); // 0-23
        int minute = random.nextInt(59); // 0-59
        int second = random.nextInt(59); // 0-59
        return convertToLocalDateToDate(
                LocalDate.of(currentYear, currentMonth, day), hour, minute, second
        );
    }

    public static String generateRandomDate(String condition) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (condition.contains(">=") || condition.contains(">")) {
            String dateString = condition.replaceAll(">=", "");
            dateString = dateString.replaceAll(">", "");
            dateString = dateString.replaceAll("'", "");
            try {
                Date date = sdf.parse(dateString);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year0 = calendar.get(Calendar.YEAR);
                int month0 = calendar.get(Calendar.MONTH) + 1;
                Random random = new Random();
                int currentYear = random.nextInt((2099 - year0) + 1) + year0;
                int currentMonth = random.nextInt((12 - month0) + 1) + month0;
                int day = random.nextInt(getDaysInMonth(currentYear, currentMonth)) + 1;
                int hour = random.nextInt(year0); // 0-23
                int minute = random.nextInt(month0); // 0-59
                int second = random.nextInt(59); // 0-59
                return convertToLocalDateToDate(
                        LocalDate.of(currentYear, currentMonth, day), hour, minute, second
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (condition.contains("<=") || condition.contains("<") || condition.contains("!=") || condition.startsWith("NOT IN")) {
            String dateString = condition.replaceAll(">=", "");
            dateString = dateString.replaceAll("<", "");
            dateString = dateString.replaceAll("!=", "");
            dateString = dateString.replaceAll("NOT IN", "");
            dateString = dateString.replaceAll("'", "");
            try {
                Date date = sdf.parse(dateString);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year0 = calendar.get(Calendar.YEAR);
                Random random = new Random();
                int currentYear = random.nextInt((year0 - 1992) + 1) + 1992;
                int currentMonth = random.nextInt(12);
                int day = random.nextInt(getDaysInMonth(currentYear, currentMonth)) + 1;
                int hour = random.nextInt(23); // 0-23
                int minute = random.nextInt(59); // 0-59
                int second = random.nextInt(59); // 0-59
                return convertToLocalDateToDate(
                        LocalDate.of(currentYear, currentMonth, day), hour, minute, second
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (condition.contains("IN") || condition.startsWith("LIKE")) {
            String dateString = condition.replaceAll("IN", "");
            dateString = dateString.replaceAll("LIKE", "");
            dateString = dateString.replaceAll("'", "");
            String[] split = dateString.split(",");
            Random random = new Random();
            int i = random.nextInt(split.length);
            dateString = split[i];
            try {
                Date date = sdf.parse(dateString);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year0 = calendar.get(Calendar.YEAR);
                int month0 = calendar.get(Calendar.MONTH) + 1;
                int day0 = calendar.get(Calendar.DAY_OF_MONTH);
                int hour0 = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                return convertToLocalDateToDate(
                        LocalDate.of(year0, month0, day0), hour0, minute, 0
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                condition = condition.replaceAll("'", "");
                Date date = sdf.parse(condition);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year0 = calendar.get(Calendar.YEAR);
                int month0 = calendar.get(Calendar.MONTH) + 1;
                int day0 = calendar.get(Calendar.DAY_OF_MONTH);
                int hour0 = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                return convertToLocalDateToDate(
                        LocalDate.of(year0, month0, day0), hour0, minute, 0
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T> T getRandomValueFromList(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List is empty.");
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    private static int getDaysInMonth(int year, int month) {
        return LocalDate.of(year, month, 1).lengthOfMonth();
    }

    private static String convertToLocalDateToDate(LocalDate localDate, int hour, int minute, int second) {
        String formattedDate = String.format("%s %02d:%02d:%02d", localDate.toString(), hour, minute, second);
        return "TO_DATE('" + formattedDate + "', 'YYYY-MM-DD HH24:MI:SS')";
    }

    public static String generateRandomValue(ColumnDefinition columnDefinition, String condition) {
        String dataType = columnDefinition.getColDataType().getDataType().toLowerCase();
        List<String> argumentsStringList = columnDefinition.getColDataType().getArgumentsStringList();
        int length = argumentsStringList != null ? Integer.parseInt(argumentsStringList.get(0)) : 1;
        length = Math.min(length, 15);
        int scale = 0;
        if (argumentsStringList != null && argumentsStringList.size() > 1) {
            scale = Integer.parseInt(argumentsStringList.get(1));
        }

        if (condition != null && !condition.isEmpty()) {
            if (condition.contains(">=")) {
                int minValue = Integer.parseInt(condition.replaceAll("[^\\d.]+", ""));
                return generateGreaterOrEqualValue(dataType, length, scale, minValue, condition);
            } else if (condition.contains(">")) {
                int minValue = Integer.parseInt(condition.replaceAll("[^\\d.]+", ""));
                return generateGreaterValue(dataType, length, scale, minValue);
            } else if (condition.startsWith("=")) {
                String defaultValue = condition.replaceFirst(".*=\\s*", "");
                return generateEqualValue(dataType, length, scale, defaultValue);
            } else if (condition.contains("<=")) {
                int maxValue = Integer.parseInt(condition.replaceAll("[^\\d.]+", ""));
                return generateLessOrEqualValue(dataType, length, scale, maxValue);
            } else if (condition.contains("<")) {
                int maxValue = Integer.parseInt(condition.replaceAll("[^\\d.]+", ""));
                return generateLessValue(dataType, length, scale, maxValue);
            } else if (condition.contains("!=")) {
                String notEqualValue = condition.replaceAll("[^\\d.]+", "");
                return generateNotEqualValue(dataType, length, scale, notEqualValue);
            } else if (condition.startsWith("LIKE")) {
                String pattern = condition.replaceAll("LIKE\\s*", "");
                return generateLikeValue(dataType, length, pattern);
            } else if (condition.startsWith("IN")) {
                List<String> values = parseInCondition(condition);
                return generateInValue(values);
            } else if (condition.startsWith("NOT IN")) {
                List<String> values = parseInCondition(condition);
                return generateNotInValue(columnDefinition, values);
            }
        }
        return generateRandomValue(columnDefinition);
    }

    public static List<String> parseInCondition(String input) {
        List<String> valuesList = new ArrayList<>();

        // 定义正则表达式匹配数字部分
        Pattern pattern = Pattern.compile("'([^']+)'");
        Matcher matcher = pattern.matcher(input);
        // 使用正则表达式匹配数字
        while (matcher.find()) {
            String matchedValue = matcher.group(1);
            valuesList.add(matchedValue);
        }

        return valuesList;
    }

    private static String generateInValue(List<String> values) {
        // 在值列表中随机选择一个值作为结果
        int randomIndex = new Random().nextInt(values.size());
        return "'" + values.get(randomIndex) + "'";
    }

    private static String generateNotInValue(ColumnDefinition columnDefinition, List<String> values) {
        // 生成一个不在值列表中的随机值作为结果
        String randomValue;
        do {
            randomValue = generateRandomValue(columnDefinition);
        } while (values.contains(randomValue));
        return "'" + randomValue + "'";
    }

    private static String generateLikeValue(String dataType, int length, String pattern) {
        if (dataType.contains("varchar") || dataType.contains("char")) {
            StringBuilder result = new StringBuilder();
            int patternLength = pattern.length();

            for (int i = 0; i < patternLength; i++) {
                char c = pattern.charAt(i);

                if (c == '%') {
                    // 处理通配符 %
                    result.append(generateRandomString(2));
                } else {
                    // 非通配符字符直接添加到结果中
                    result.append(c);
                }
            }

            // 如果结果中没有字符，生成一个随机字符串
            if (result.length() == 0) {
                result.append(generateRandomString(2));
            }

            return "'" + result.toString() + "'";
        } else {
            // 非字符串类型的数据默认返回 NULL
            return "NULL";
        }
    }

    private static String generateNotEqualValue(String dataType, int length, int scale, String notEqualValue) {
        Random random = new Random();

        if (dataType.contains("int") || dataType.contains("numeric")) {
            int randomValue;
            do {
                randomValue = random.nextInt(Integer.parseInt(notEqualValue)); // 生成一个随机整数
            } while (String.valueOf(randomValue).equals(notEqualValue)); // 重复生成，直到不等于指定值
            return String.valueOf(randomValue);
        } else if (dataType.contains("decimal") || dataType.contains("number")) {
            String randomValue;
            do {
                randomValue = String.valueOf(random.nextDouble() * 100); // 生成一个随机浮点数
                randomValue = String.format("%." + scale + "f", Double.valueOf(randomValue));
            } while (String.valueOf(randomValue).equals(notEqualValue)); // 重复生成，直到不等于指定值
            return String.format("%." + scale + "f", Double.valueOf(randomValue));
        } else if (dataType.contains("date") || dataType.contains("timestamp")) {
            return generateRandomDate();
        } else {
            return "NULL";
        }
    }

    private static String generateLessValue(String dataType, int length, int scale, double maxValue) {
        Random random = new Random();

        if (dataType.contains("int") || dataType.contains("numeric")) {
            int randomValue = random.nextInt((int) maxValue); // 生成一个小于最大值的随机整数
            return String.valueOf(randomValue);
        } else if (dataType.contains("decimal") || dataType.contains("number")) {
            double randomValue = random.nextDouble() * (maxValue); // 生成一个小于最大值的随机浮点数
            return String.format("%." + scale + "f", randomValue);
        } else if (dataType.contains("date") || dataType.contains("timestamp")) {
            // 生成随机日期
            // 此处可以调用生成日期的方法，根据需要返回日期字符串
            return generateRandomDate();
        } else {
            return "NULL";
        }
    }

    private static String generateLessOrEqualValue(String dataType, int length, int scale, double maxValue) {
        Random random = new Random();

        if (dataType.contains("int") || dataType.contains("numeric")) {
            int randomValue = random.nextInt((int) maxValue + 1); // 生成一个小于等于最大值的随机整数
            return String.valueOf(randomValue);
        } else if (dataType.contains("decimal") || dataType.contains("number")) {
            double randomValue = random.nextDouble() * (maxValue + 1); // 生成一个小于等于最大值的随机浮点数
            return String.format("%." + scale + "f", randomValue);
        } else if (dataType.contains("date") || dataType.contains("timestamp")) {
            // 生成随机日期
            // 此处可以调用生成日期的方法，根据需要返回日期字符串
            return generateRandomDate();
        } else {
            return "NULL";
        }
    }

    private static String generateGreaterOrEqualValue(String dataType, int length, int scale, int minValue, String condition) {
        if (dataType.contains("int") || dataType.contains("numeric")) {
            int randomValue = minValue + new Random().nextInt(100 - minValue + 1); // 生成一个大于等于 minValue 的随机整数
            return String.valueOf(randomValue);
        } else if (dataType.contains("decimal") || dataType.contains("number")) {
            double randomValue = minValue + new Random().nextDouble() * (100 - minValue); // 生成一个大于等于 minValue 的随机浮点数
            return String.format("%.2f", randomValue);
        } else if (dataType.contains("date") || dataType.contains("timestamp")) {
            return generateRandomDate(condition);
        } else {
            return "NULL";
        }
    }

    private static String generateGreaterValue(String dataType, int length, int scale, int minValue) {
        if (dataType.contains("int") || dataType.contains("numeric")) {
            int randomValue = minValue + new Random().nextInt(100 - minValue); // 生成一个大于 minValue 的随机整数
            return String.valueOf(randomValue);
        } else if (dataType.contains("decimal") || dataType.contains("number")) {
            double randomValue = minValue + new Random().nextDouble() * (100 - minValue); // 生成一个大于 minValue 的随机浮点数
            return formatDecimalValue(randomValue, length, scale);
        } else if (dataType.contains("date") || dataType.contains("timestamp")) {
            return generateRandomDate();
        } else {
            return "NULL";
        }
    }

    private static String formatDecimalValue(double value, int length, int scale) {
        String format = "%." + scale + "f";
        return String.format(format, value);
    }

    private static String generateEqualValue(String dataType, int length, int scale, Object equalValue) {
        if (equalValue == null) {
            return "NULL"; // 如果等于值为 null，则返回 "NULL"
        } else if (equalValue instanceof Integer) {
            return String.valueOf(equalValue); // 如果等于值为整数，返回整数的字符串表示
        } else if (equalValue instanceof Double) {
            return String.format("%." + scale + "f", equalValue); // 如果等于值为浮点数，返回浮点数的字符串表示
        } else if (equalValue instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return "TO_DATE('" + dateFormat.format(equalValue) + "', 'YYYY-MM-DD HH24:MI:SS')"; // 如果等于值为日期，返回日期的字符串表示
        } else {
            return "'" + equalValue.toString() + "'"; // 对于其他数据类型，返回字符串表示
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {

            Random random = new Random();
            int randomValue = random.nextInt(25);
            System.out.println("替换后字符串: " + randomValue);

        }
    }

}

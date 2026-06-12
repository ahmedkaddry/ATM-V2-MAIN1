package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static String readFile(String path) {
        Path filePath = Path.of(path);
        if (Files.exists(filePath)) {
            return readString(filePath);
        }

        Path resolved = resolveFile(path);
        if (resolved != null) {
            return readString(resolved);
        }

        return null;
    }

    private static String readString(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return null;
        }
    }

    public static void writeFile(String path, String content) {
        Path target = Path.of(path);
        if (!Files.exists(target)) {
            Path resolved = resolveFile(path);
            if (resolved != null) {
                target = resolved;
            } else if (target.getParent() != null) {
                Path resolvedDir = resolveDirectory(target.getParent());
                if (resolvedDir != null) {
                    target = resolvedDir.resolve(target.getFileName());
                }
            }
        }

        try {
            if (target.getParent() != null) {
                Files.createDirectories(target.getParent());
            }
            Files.writeString(target, content);
        } catch (IOException e) {
            System.out.println("Failed to write file: " + target);
        }
    }

    private static Path resolveFile(String path) {
        Path workingDir = Path.of(System.getProperty("user.dir")).normalize();
        List<Path> rootCandidates = List.of(
                Path.of(""),
                Path.of("version-two"),
                Path.of("ATM-V2-main", "version-two"),
                Path.of("ATM-V2-main (1)", "ATM-V2-main", "version-two")
        );

        for (Path dir = workingDir; dir != null; dir = dir.getParent()) {
            for (Path root : rootCandidates) {
                Path candidate = dir.resolve(root).resolve(path);
                if (Files.exists(candidate)) {
                    return candidate;
                }
            }
        }

        try {
            Path codeSource = Path.of(JsonUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).normalize();
            Path parent = Files.isRegularFile(codeSource) ? codeSource.getParent() : codeSource;
            for (Path dir = parent; dir != null; dir = dir.getParent()) {
                for (Path root : rootCandidates) {
                    Path candidate = dir.resolve(root).resolve(path);
                    if (Files.exists(candidate)) {
                        return candidate;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static Path resolveDirectory(Path path) {
        Path workingDir = Path.of(System.getProperty("user.dir")).normalize();
        List<Path> rootCandidates = List.of(
                Path.of(""),
                Path.of("version-two"),
                Path.of("ATM-V2-main", "version-two"),
                Path.of("ATM-V2-main (1)", "ATM-V2-main", "version-two")
        );

        for (Path dir = workingDir; dir != null; dir = dir.getParent()) {
            for (Path root : rootCandidates) {
                Path candidate = dir.resolve(root).resolve(path);
                if (Files.exists(candidate)) {
                    return candidate;
                }
            }
        }

        try {
            Path codeSource = Path.of(JsonUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).normalize();
            Path parent = Files.isRegularFile(codeSource) ? codeSource.getParent() : codeSource;
            for (Path dir = parent; dir != null; dir = dir.getParent()) {
                for (Path root : rootCandidates) {
                    Path candidate = dir.resolve(root).resolve(path);
                    if (candidate != null && Files.exists(candidate)) {
                        return candidate;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static JsonObject readJSON(String path) {
        String content = readFile(path);
        if (content == null || content.isEmpty()) {
            return null;
        }
        return new JsonParser(content).parseObject();
    }

    public static JsonArray readArray(String path) {
        String content = readFile(path);
        if (content == null || content.isEmpty()) {
            return null;
        }
        return new JsonParser(content).parseArray();
    }

    public static class JsonObject {
        private final Map<String, Object> map = new LinkedHashMap<>();

        public JsonObject() {
        }

        JsonObject(Map<String, Object> values) {
            map.putAll(values);
        }

        public boolean has(String key) {
            return map.containsKey(key);
        }

        public int getInt(String key) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            throw new IllegalStateException("Value is not an int: " + key);
        }

        public int optInt(String key, int defaultValue) {
            if (!map.containsKey(key)) return defaultValue;
            Object value = map.get(key);
            if (value instanceof Number) return ((Number) value).intValue();
            if (value instanceof String) return Integer.parseInt((String) value);
            return defaultValue;
        }

        public double getDouble(String key) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
            throw new IllegalStateException("Value is not a double: " + key);
        }

        public String getString(String key) {
            Object value = map.get(key);
            if (value == null) return null;
            return value.toString();
        }

        public boolean getBoolean(String key) {
            Object value = map.get(key);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            throw new IllegalStateException("Value is not a boolean: " + key);
        }

        public JsonObject getJSONObject(String key) {
            Object value = map.get(key);
            if (value instanceof JsonObject) {
                return (JsonObject) value;
            }
            return null;
        }

        public void put(String key, Object value) {
            map.put(key, value);
        }

        public String toString(int indentFactor) {
            return toString(indentFactor, 0);
        }

        private String toString(int indentFactor, int level) {
            if (map.isEmpty()) {
                return "{}";
            }
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            String indent = "".repeat(level + indentFactor);
            String outerIndent = "".repeat(level);
            boolean first = true;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) sb.append(',');
                sb.append('\n').append(indent);
                sb.append('"').append(escape(entry.getKey())).append('"').append(": ");
                sb.append(valueToString(entry.getValue(), indentFactor, level + indentFactor));
                first = false;
            }
            sb.append('\n').append(outerIndent).append('}');
            return sb.toString();
        }
    
        private String valueToString(Object value, int indentFactor, int level) {
            if (value == null) return "null";
            if (value instanceof String) return '"' + escape(value.toString()) + '"';
            if (value instanceof Number || value instanceof Boolean) return value.toString();
            if (value instanceof JsonObject) return ((JsonObject) value).toString(indentFactor, level);
            if (value instanceof JsonArray) return ((JsonArray) value).toString(indentFactor, level);
            return '"' + escape(value.toString()) + '"';
        }

        private String escape(String value) {
            return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }
    }

    public static class JsonArray {
        private final List<Object> items = new ArrayList<>();

        public JsonArray() {
        }

        JsonArray(List<Object> values) {
            items.addAll(values);
        }

        public int length() {
            return items.size();
        }

        public JsonObject getJSONObject(int index) {
            Object value = items.get(index);
            if (value instanceof JsonObject) {
                return (JsonObject) value;
            }
            return null;
        }

        public String getString(int index) {
            Object value = items.get(index);
            return value == null ? null : value.toString();
        }

        public int getInt(int index) {
            Object value = items.get(index);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            throw new IllegalStateException("Value is not an int: " + index);
        }

        public boolean getBoolean(int index) {
            Object value = items.get(index);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            throw new IllegalStateException("Value is not a boolean: " + index);
        }

        public void put(Object value) {
            items.add(value);
        }

        public String toString(int indentFactor) {
            return toString(indentFactor, 0);
        }

        private String toString(int indentFactor, int level) {
            if (items.isEmpty()) {
                return "[]";
            }
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            String indent = "".repeat(level + indentFactor);
            String outerIndent = "".repeat(level);
            boolean first = true;
            for (Object item : items) {
                if (!first) sb.append(',');
                sb.append('\n').append(indent);
                sb.append(valueToString(item, indentFactor, level + indentFactor));
                first = false;
            }
            sb.append('\n').append(outerIndent).append(']');
            return sb.toString();
        }

        private String valueToString(Object value, int indentFactor, int level) {
            if (value == null) return "null";
            if (value instanceof String) return '"' + escape(value.toString()) + '"';
            if (value instanceof Number || value instanceof Boolean) return value.toString();
            if (value instanceof JsonObject) return ((JsonObject) value).toString(indentFactor, level);
            if (value instanceof JsonArray) return ((JsonArray) value).toString(indentFactor, level);
            return '"' + escape(value.toString()) + '"';
        }

        private String escape(String value) {
            return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }
    }

    private static class JsonParser {
        private final String json;
        private int pos;

        JsonParser(String json) {
            this.json = json;
            this.pos = 0;
        }

        JsonObject parseObject() {
            skipWhitespace();
            if (peek() != '{') {
                throw new IllegalStateException("Expected object start");
            }
            pos++;
            skipWhitespace();
            Map<String, Object> map = new LinkedHashMap<>();
            if (peek() == '}') {
                pos++;
                return new JsonObject(map);
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                skipWhitespace();
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                if (peek() == ',') {
                    pos++;
                    continue;
                }
                if (peek() == '}') {
                    pos++;
                    break;
                }
                throw new IllegalStateException("Expected , or } in object");
            }
            return new JsonObject(map);
        }

        JsonArray parseArray() {
            skipWhitespace();
            if (peek() != '[') {
                throw new IllegalStateException("Expected array start");
            }
            pos++;
            skipWhitespace();
            List<Object> items = new ArrayList<>();
            if (peek() == ']') {
                pos++;
                return new JsonArray(items);
            }
            while (true) {
                skipWhitespace();
                items.add(parseValue());
                skipWhitespace();
                if (peek() == ',') {
                    pos++;
                    continue;
                }
                if (peek() == ']') {
                    pos++;
                    break;
                }
                throw new IllegalStateException("Expected , or ] in array");
            }
            return new JsonArray(items);
        }

        private Object parseValue() {
            skipWhitespace();
            char c = peek();
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == '"') return parseString();
            if (c == 't') {
                expectLiteral("true");
                return Boolean.TRUE;
            }
            if (c == 'f') {
                expectLiteral("false");
                return Boolean.FALSE;
            }
            if (c == 'n') {
                expectLiteral("null");
                return null;
            }
            return parseNumber();
        }

        private String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = next();
                if (c == '"') break;
                if (c == '\\') {
                    char escaped = next();
                    switch (escaped) {
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/' -> sb.append('/');
                        case 'b' -> sb.append('\b');
                        case 'f' -> sb.append('\f');
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 't' -> sb.append('\t');
                        case 'u' -> sb.append((char) Integer.parseInt(json.substring(pos, pos + 4), 16));
                        default -> sb.append(escaped);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private Number parseNumber() {
            int start = pos;
            if (pos < json.length() && json.charAt(pos) == '-') pos++;
            while (pos < json.length() && Character.isDigit(json.charAt(pos))) pos++;
            if (pos < json.length() && json.charAt(pos) == '.') {
                pos++;
                while (pos < json.length() && Character.isDigit(json.charAt(pos))) pos++;
            }
            if (pos < json.length() && (json.charAt(pos) == 'e' || json.charAt(pos) == 'E')) {
                pos++;
                if (pos < json.length() && (json.charAt(pos) == '+' || json.charAt(pos) == '-')) pos++;
                while (pos < json.length() && Character.isDigit(json.charAt(pos))) pos++;
            }
            String value = json.substring(start, pos);
            if (value.contains(".") || value.contains("e") || value.contains("E")) {
                return Double.parseDouble(value);
            }
            return Long.parseLong(value);
        }

        private void expect(char expected) {
            if (peek() != expected) {
                throw new IllegalStateException("Expected '" + expected + "' but found '" + peek() + "'");
            }
            pos++;
        }

        private void expectLiteral(String literal) {
            for (char c : literal.toCharArray()) {
                expect(c);
            }
        }

        private char peek() {
            skipWhitespace();
            if (pos >= json.length()) {
                return '\0';
            }
            return json.charAt(pos);
        }

        private char next() {
            if (pos >= json.length()) {
                return '\0';
            }
            return json.charAt(pos++);
        }

        private void skipWhitespace() {
            while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
                pos++;
            }
        }
    }
}

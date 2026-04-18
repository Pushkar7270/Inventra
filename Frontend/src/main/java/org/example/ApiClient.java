package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {

    private static final String BASE = "http://localhost:8080/api";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    public static List<Map<String, Object>> getProducts() throws Exception {
        Object parsed = Json.parse(get("/products"));
        if (!(parsed instanceof List)) {
            throw new IOException("Expected JSON array for products");
        }
        return castListOfMaps((List<?>) parsed);
    }

    public static Map<String, Object> addProduct(String name, String sku, double price, int stock, int threshold)
            throws Exception {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("name", name);
        payload.put("skuCode", sku);
        payload.put("price", price);
        payload.put("currentStock", stock);
        payload.put("reorderThreshold", threshold);

        Object parsed = Json.parse(postJson("/products", Json.stringify(payload)));
        if (!(parsed instanceof Map)) {
            throw new IOException("Expected JSON object for created product");
        }
        return castMap((Map<?, ?>) parsed);
    }

    public static Map<String, Object> sellProduct(long id, int qty) throws Exception {
        Object parsed = Json.parse(put("/products/" + id + "/sell?quantity=" + qty, null, null));
        if (!(parsed instanceof Map)) {
            throw new IOException("Expected JSON object for sell response");
        }
        return castMap((Map<?, ?>) parsed);
    }

    public static Map<String, Object> updateProduct(long id, String name, String sku, double price, int threshold)
            throws Exception {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("name", name);
        payload.put("skuCode", sku);
        payload.put("price", price);
        payload.put("reorderThreshold", threshold);

        byte[] body = Json.stringify(payload).getBytes(UTF_8);
        Object parsed = Json.parse(put("/products/" + id, "application/json", body));
        if (!(parsed instanceof Map)) {
            throw new IOException("Expected JSON object for updated product");
        }
        return castMap((Map<?, ?>) parsed);
    }

    public static void deleteProduct(long id) throws Exception {
        request("DELETE", "/products/" + id, null, null);
    }

    public static Map<String, Object> getProductBySku(String sku) throws Exception {
        String path = "/products/sku/" + encodePathSegment(sku);
        Object parsed = Json.parse(get(path));
        if (!(parsed instanceof Map)) {
            throw new IOException("Expected JSON object for product");
        }
        return castMap((Map<?, ?>) parsed);
    }

    public static List<Map<String, Object>> getLowStock() throws Exception {
        Object parsed = Json.parse(get("/products/low-stock"));
        if (!(parsed instanceof List)) {
            throw new IOException("Expected JSON array for low-stock response");
        }
        return castListOfMaps((List<?>) parsed);
    }

    public static byte[] downloadBill(long transactionId) throws Exception {
        return requestBytes("GET", "/transactions/" + transactionId + "/bill", null, null);
    }

    public static byte[] downloadExcel() throws Exception {
        return requestBytes("GET", "/transactions/export", null, null);
    }

    private static String get(String path) throws IOException {
        return request("GET", path, null, null);
    }

    private static String postJson(String path, String json) throws IOException {
        return request("POST", path, "application/json", json.getBytes(UTF_8));
    }

    private static String put(String path, String contentType, byte[] body) throws IOException {
        return request("PUT", path, contentType, body);
    }

    private static String request(String method, String path, String contentType, byte[] body) throws IOException {
        byte[] bytes = requestBytes(method, path, contentType, body);
        return new String(bytes, UTF_8);
    }

    private static byte[] requestBytes(String method, String path, String contentType, byte[] body) throws IOException {
        URL url = new URL(BASE + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(30_000);

        if (contentType != null) {
            conn.setRequestProperty("Content-Type", contentType);
        }
        conn.setRequestProperty("Accept", "application/json");

        if (body != null) {
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(body.length);
            OutputStream os = null;
            try {
                os = conn.getOutputStream();
                os.write(body);
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException ignored) {
                        // ignore
                    }
                }
            }
        }

        int status = conn.getResponseCode();
        InputStream is = null;
        try {
            is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
            byte[] response = readAllBytes(is);
            if (status < 200 || status >= 300) {
                String message = new String(response, UTF_8);
                throw new IOException("HTTP " + status + " for " + method + " " + path + ": " + message);
            }
            return response;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                    // ignore
                }
            }
            conn.disconnect();
        }
    }

    private static byte[] readAllBytes(InputStream is) throws IOException {
        if (is == null) {
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int read;
        while ((read = is.read(buf)) != -1) {
            baos.write(buf, 0, read);
        }
        return baos.toByteArray();
    }

    private static String encodePathSegment(String segment) throws IOException {
        return URLEncoder.encode(segment, "UTF-8").replace("+", "%20");
    }

    private static List<Map<String, Object>> castListOfMaps(List<?> list) throws IOException {
        List<Map<String, Object>> out = new ArrayList<Map<String, Object>>(list.size());
        for (Object item : list) {
            if (!(item instanceof Map)) {
                throw new IOException("Expected array of objects");
            }
            out.add(castMap((Map<?, ?>) item));
        }
        return out;
    }

    private static Map<String, Object> castMap(Map<?, ?> map) throws IOException {
        Map<String, Object> out = new HashMap<String, Object>(map.size());
        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (!(e.getKey() instanceof String)) {
                throw new IOException("Expected string keys in object");
            }
            out.put((String) e.getKey(), e.getValue());
        }
        return out;
    }

    private static final class Json {
        private Json() {
        }

        static Object parse(String json) throws IOException {
            if (json == null) {
                return null;
            }
            Parser parser = new Parser(json);
            Object value = parser.parseValue();
            parser.skipWhitespace();
            if (!parser.isEof()) {
                throw new IOException("Trailing characters in JSON");
            }
            return value;
        }

        static String stringify(Object value) throws IOException {
            StringBuilder sb = new StringBuilder();
            Writer.writeValue(sb, value);
            return sb.toString();
        }

        private static final class Writer {
            private Writer() {
            }

            static void writeValue(StringBuilder sb, Object value) throws IOException {
                if (value == null) {
                    sb.append("null");
                    return;
                }
                if (value instanceof String) {
                    writeString(sb, (String) value);
                    return;
                }
                if (value instanceof Number) {
                    sb.append(value.toString());
                    return;
                }
                if (value instanceof Boolean) {
                    sb.append(((Boolean) value).booleanValue() ? "true" : "false");
                    return;
                }
                if (value instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> map = (Map<Object, Object>) value;
                    sb.append('{');
                    boolean first = true;
                    for (Map.Entry<Object, Object> e : map.entrySet()) {
                        if (!(e.getKey() instanceof String)) {
                            throw new IOException("Only string keys supported in JSON objects");
                        }
                        if (!first) {
                            sb.append(',');
                        }
                        first = false;
                        writeString(sb, (String) e.getKey());
                        sb.append(':');
                        writeValue(sb, e.getValue());
                    }
                    sb.append('}');
                    return;
                }
                if (value instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> list = (List<Object>) value;
                    sb.append('[');
                    for (int i = 0; i < list.size(); i++) {
                        if (i > 0) {
                            sb.append(',');
                        }
                        writeValue(sb, list.get(i));
                    }
                    sb.append(']');
                    return;
                }
                throw new IOException("Unsupported JSON type: " + value.getClass().getName());
            }

            private static void writeString(StringBuilder sb, String s) {
                sb.append('"');
                for (int i = 0; i < s.length(); i++) {
                    char c = s.charAt(i);
                    switch (c) {
                        case '"':
                            sb.append("\\\"");
                            break;
                        case '\\':
                            sb.append("\\\\");
                            break;
                        case '\b':
                            sb.append("\\b");
                            break;
                        case '\f':
                            sb.append("\\f");
                            break;
                        case '\n':
                            sb.append("\\n");
                            break;
                        case '\r':
                            sb.append("\\r");
                            break;
                        case '\t':
                            sb.append("\\t");
                            break;
                        default:
                            if (c < 0x20) {
                                sb.append("\\u");
                                String hex = Integer.toHexString(c);
                                for (int j = hex.length(); j < 4; j++) {
                                    sb.append('0');
                                }
                                sb.append(hex);
                            } else {
                                sb.append(c);
                            }
                    }
                }
                sb.append('"');
            }
        }

        private static final class Parser {
            private final String s;
            private int i;

            Parser(String s) {
                this.s = s;
                this.i = 0;
            }

            boolean isEof() {
                return i >= s.length();
            }

            void skipWhitespace() {
                while (!isEof()) {
                    char c = s.charAt(i);
                    if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                        i++;
                    } else {
                        break;
                    }
                }
            }

            Object parseValue() throws IOException {
                skipWhitespace();
                if (isEof()) {
                    throw new IOException("Unexpected end of JSON");
                }
                char c = s.charAt(i);
                if (c == '{') {
                    return parseObject();
                }
                if (c == '[') {
                    return parseArray();
                }
                if (c == '"') {
                    return parseString();
                }
                if (c == 't') {
                    expect("true");
                    return Boolean.TRUE;
                }
                if (c == 'f') {
                    expect("false");
                    return Boolean.FALSE;
                }
                if (c == 'n') {
                    expect("null");
                    return null;
                }
                if (c == '-' || (c >= '0' && c <= '9')) {
                    return parseNumber();
                }
                throw new IOException("Unexpected character in JSON: " + c);
            }

            private Map<String, Object> parseObject() throws IOException {
                expectChar('{');
                skipWhitespace();
                Map<String, Object> map = new HashMap<String, Object>();
                if (peekChar('}')) {
                    i++;
                    return map;
                }
                while (true) {
                    skipWhitespace();
                    String key = parseString();
                    skipWhitespace();
                    expectChar(':');
                    Object value = parseValue();
                    map.put(key, value);
                    skipWhitespace();
                    if (peekChar('}')) {
                        i++;
                        return map;
                    }
                    expectChar(',');
                }
            }

            private List<Object> parseArray() throws IOException {
                expectChar('[');
                skipWhitespace();
                List<Object> list = new ArrayList<Object>();
                if (peekChar(']')) {
                    i++;
                    return list;
                }
                while (true) {
                    Object value = parseValue();
                    list.add(value);
                    skipWhitespace();
                    if (peekChar(']')) {
                        i++;
                        return list;
                    }
                    expectChar(',');
                }
            }

            private String parseString() throws IOException {
                expectChar('"');
                StringBuilder out = new StringBuilder();
                while (true) {
                    if (isEof()) {
                        throw new IOException("Unterminated JSON string");
                    }
                    char c = s.charAt(i++);
                    if (c == '"') {
                        return out.toString();
                    }
                    if (c == '\\') {
                        if (isEof()) {
                            throw new IOException("Unterminated JSON escape");
                        }
                        char e = s.charAt(i++);
                        switch (e) {
                            case '"':
                            case '\\':
                            case '/':
                                out.append(e);
                                break;
                            case 'b':
                                out.append('\b');
                                break;
                            case 'f':
                                out.append('\f');
                                break;
                            case 'n':
                                out.append('\n');
                                break;
                            case 'r':
                                out.append('\r');
                                break;
                            case 't':
                                out.append('\t');
                                break;
                            case 'u':
                                out.append(parseUnicodeEscape());
                                break;
                            default:
                                throw new IOException("Invalid JSON escape: \\" + e);
                        }
                    } else {
                        out.append(c);
                    }
                }
            }

            private char parseUnicodeEscape() throws IOException {
                if (i + 4 > s.length()) {
                    throw new IOException("Invalid unicode escape");
                }
                int code = 0;
                for (int j = 0; j < 4; j++) {
                    char c = s.charAt(i++);
                    int val;
                    if (c >= '0' && c <= '9') {
                        val = c - '0';
                    } else if (c >= 'a' && c <= 'f') {
                        val = 10 + (c - 'a');
                    } else if (c >= 'A' && c <= 'F') {
                        val = 10 + (c - 'A');
                    } else {
                        throw new IOException("Invalid unicode escape hex: " + c);
                    }
                    code = (code << 4) | val;
                }
                return (char) code;
            }

            private Number parseNumber() throws IOException {
                int start = i;
                if (peekChar('-')) {
                    i++;
                }
                while (!isEof()) {
                    char c = s.charAt(i);
                    if (c >= '0' && c <= '9') {
                        i++;
                    } else {
                        break;
                    }
                }
                boolean isFloat = false;
                if (!isEof() && peekChar('.')) {
                    isFloat = true;
                    i++;
                    while (!isEof()) {
                        char c = s.charAt(i);
                        if (c >= '0' && c <= '9') {
                            i++;
                        } else {
                            break;
                        }
                    }
                }
                if (!isEof() && (peekChar('e') || peekChar('E'))) {
                    isFloat = true;
                    i++;
                    if (!isEof() && (peekChar('+') || peekChar('-'))) {
                        i++;
                    }
                    while (!isEof()) {
                        char c = s.charAt(i);
                        if (c >= '0' && c <= '9') {
                            i++;
                        } else {
                            break;
                        }
                    }
                }
                String raw = s.substring(start, i);
                try {
                    if (isFloat) {
                        return Double.valueOf(raw);
                    }
                    long l = Long.parseLong(raw);
                    if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                        return Integer.valueOf((int) l);
                    }
                    return Long.valueOf(l);
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid JSON number: " + raw);
                }
            }

            private void expect(String keyword) throws IOException {
                if (s.regionMatches(i, keyword, 0, keyword.length())) {
                    i += keyword.length();
                    return;
                }
                throw new IOException("Expected '" + keyword + "'");
            }

            private void expectChar(char expected) throws IOException {
                if (isEof() || s.charAt(i) != expected) {
                    throw new IOException("Expected '" + expected + "'");
                }
                i++;
            }

            private boolean peekChar(char c) {
                return !isEof() && s.charAt(i) == c;
            }
        }
    }
}

package com.hyd.sob;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Consumer;

/**
 * 发送 HTTP 请求并返回结果。每个 HttpSender
 * 对象用于向一个地址发送，每次发送可以带不同的参数。
 * <p>
 * HttpSender 不是线程安全的。
 *
 * @author yiding.he
 */
public class HttpRequest {

    public static final int DEFAULT_TIMEOUT = 60000;

    public static final String DEFAULT_CHATSET = "UTF-8";

    /////////////////////////////////////////////////////////

    private String url;

    private String basicAuth;

    private byte[] response = null;

    /**
     * 请求参数，根据请求方法是 GET 还是 POST，参数将接在 URL 后面或者放在 HTTP BODY 里
     */
    private Map<String, List<String>> parameters = new HashMap<>();

    /**
     * 请求头
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * 要上传的文件列表
     */
    private Map<String, FileInfo> files = new HashMap<>();

    /**
     * 连接和读取的超时时间（毫秒）
     */
    private int timeout = DEFAULT_TIMEOUT;

    /**
     * 编码
     */
    private String charset = DEFAULT_CHATSET;

    /**
     * 构造函数
     *
     * @param url 要发送请求的地址
     */
    public HttpRequest(String url) {
        this.url = url;
        this.timeout = DEFAULT_TIMEOUT;
    }

    /**
     * 构造函数
     *
     * @param url     要发送请求的地址
     * @param timeout 连接超时时间
     */
    public HttpRequest(String url, int timeout) {
        this.url = url;
        this.timeout = timeout;
    }

    public static String appendParams(String url, Map<String, List<String>> params, String charset) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        String string = "";
        for (String key : params.keySet()) {
            List<String> values = params.get(key);
            for (String value : values) {
                if (value != null) {
                    string += key + "=" + urlEncode(value, charset) + "&";
                }
            }
        }
        string = removeEnd(string, "&");

        if (url.contains("?")) {
            if (url.endsWith("?")) {
                return url + string;
            } else {
                return url + "&" + string;
            }
        } else {
            return url + "?" + string;
        }
    }

    private static String removeEnd(String string, String end) {
        if (string == null || end == null || string.length() < end.length()) {
            return string;
        }

        return string.endsWith(end) ? string.substring(0, string.length() - end.length()) : string;
    }

    public static String urlEncode(String str, String charset) {
        if (str == null) {
            return null;
        }

        try {
            return URLEncoder.encode(str, charset);
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    public static byte[] read(InputStream inputStream) {
        try {
            if (inputStream == null) {
                return new byte[0];
            }

            byte[] content = new byte[0];
            byte[] buffer = new byte[40960];
            int size;

            while ((size = inputStream.read(buffer)) > -1) {
                byte[] newContent = new byte[content.length + size];
                System.arraycopy(content, 0, newContent, 0, content.length);
                System.arraycopy(buffer, 0, newContent, content.length, size);
                content = newContent;
            }

            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBasicAuth(String username, String password) {
        this.basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public void clearBasicAuth() {
        this.basicAuth = null;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getUrl() {
        return url;
    }

    public byte[] getResponse() {
        return response;
    }

    public String getResponseString() {
        try {
            return new String(response, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 设置请求参数
     *
     * @param name   参数名
     * @param values 参数值
     *
     * @return HttpRequest 对象本身
     */
    public HttpRequest setParameter(String name, Object... values) {
        if (values != null) {
            List<String> params = parameters.get(name);
            if (params == null) {
                params = new ArrayList<>();
                parameters.put(name, params);
            }

            for (Object value : values) {
                if (value != null) {
                    params.add(String.valueOf(value));
                }
            }
        }
        return this;
    }

    /**
     * 设置 header
     *
     * @param name  名称
     * @param value 值
     *
     * @return HttpRequest 对象本身
     */
    public HttpRequest setHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    /**
     * 发送 POST 请求，结果保存在 {@link #response} 中。如果有文件要上传，则需要调用本方法
     *
     * @throws IOException 如果发送请求或读取结果失败
     */
    private void sendPost0() {
        try {
            if (files.isEmpty()) {
                sendSinglePartRequest(true, null, this::readInputStream);
            } else {
                sendMultipartRequest();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送 POST 请求，将 content 作为 HTTP BODY 发送。本方法不会上传文件
     *
     * @param content 要提交的内容
     *
     * @return 服务器响应
     *
     * @throws IOException 如果请求失败
     */
    public String requestPost(String content) {
        try {
            sendSinglePartRequest(true, content, this::readInputStream);
            return getResponseString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送 POST 请求。如果有文件待上传则会上传文件
     *
     * @return 服务器响应
     *
     * @throws IOException 如果请求失败
     */
    public String requestPost() throws IOException {
        sendPost0();
        return getResponseString();
    }

    /**
     * 发送 GET 请求
     *
     * @return 服务器响应
     *
     * @throws IOException 如果请求失败
     */
    public String request() throws IOException {
        sendSinglePartRequest(false, null, this::readInputStream);
        return getResponseString();
    }

    private void readInputStream(InputStream inputStream) {
        this.response = read(inputStream);
    }

    /**
     * 发送普通请求
     *
     * @param post                是否是 POST
     * @param content             HTTP 请求体内容。如果不为空则忽略所有其他设置的参数
     * @param inputStreamConsumer 如何处理服务器返回的内容
     *
     * @throws IOException 如果请求失败
     */
    private void sendSinglePartRequest(
            boolean post, String content, Consumer<InputStream> inputStreamConsumer
    ) throws IOException {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        String _url;
        if (post) {
            _url = url;
        } else {
            _url = appendParams(url, parameters, charset);
        }

        try {
            HttpURLConnection connection = createURLConnection(_url);

            for (String headerName : headers.keySet()) {
                String headerValue = headers.get(headerName);
                connection.setRequestProperty(headerName, headerValue);
            }

            if (post) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.connect();

                String bodyString;

                if (content == null) {
                    bodyString = appendParams("", parameters, charset);
                    if (bodyString.startsWith("?")) {
                        bodyString = bodyString.substring(1);
                    }
                } else {
                    bodyString = content;
                }

                outputStream = connection.getOutputStream();
                outputStream.write(bodyString.getBytes("UTF-8"));
                outputStream.flush();
            } else {
                connection.setRequestMethod("GET");
                connection.connect();
            }

            try {
                inputStream = connection.getInputStream();
                inputStreamConsumer.accept(inputStream);
            } catch (IOException e) {
                if (connection.getErrorStream() != null) {
                    this.response = read(connection.getErrorStream());
                }
                throw e;
            }
        } finally {
            close(inputStream, outputStream);
        }
    }

    private void sendMultipartRequest() throws IOException {

        String boundary = MultipartOutputStream.createBoundary();
        HttpURLConnection connection = null;

        try {
            connection = createURLConnection(url);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = connection.getOutputStream();
            MultipartOutputStream mos = new MultipartOutputStream(outputStream, boundary);

            for (String key : parameters.keySet()) {
                List<String> values = parameters.get(key);
                for (String value : values) {
                    if (value != null) {
                        mos.writeField(key, value);
                    }
                }
            }

            for (String key : files.keySet()) {
                FileInfo file = files.get(key);
                String contentType = new MimetypesFileTypeMap().getContentType(file.getFilename());
                mos.writeFile(key, contentType, file.getFilename(), file.getFiledata());
            }

            mos.close();

            try {
                InputStream inputStream = connection.getInputStream();
                this.response = read(inputStream);
            } catch (IOException e) {
                if (connection.getErrorStream() != null) {
                    this.response = read(connection.getErrorStream());
                }
                throw e;
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpURLConnection createURLConnection(String url) throws IOException {
        HttpURLConnection connection;
        connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);

        if (basicAuth != null) {
            connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        }

        return connection;
    }

    private void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    // nothing to do
                }
            }
        }
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    /**
     * 添加一个要上传的文件
     *
     * @param fieldName 参数名
     * @param filename  文件名
     * @param data      文件内容
     */
    public void addFile(String fieldName, String filename, byte[] data) {
        files.put(fieldName, new FileInfo(filename, data));
    }

    public void putAll(Map<String, List<String>> params) {
        if (params == null || params.isEmpty()) {
            return;
        }

        for (String key : params.keySet()) {
            List<String> values = params.get(key);
            for (String value : values) {
                setParameter(key, value);
            }
        }
    }

    ////////////////////////////////////////////////////////////////

    private static class FileInfo {

        private String filename;

        private byte[] filedata;

        private FileInfo(String filename, byte[] filedata) {
            this.filename = filename;
            this.filedata = filedata;
        }

        public String getFilename() {
            return filename;
        }

        public byte[] getFiledata() {
            return filedata;
        }
    }

    ////////////////////////////////////////////////////////////////

    public static class MultipartOutputStream {

        /**
         * The line end characters.
         */
        private static final String NEWLINE = "\r\n";

        /**
         * The boundary prefix.
         */
        private static final String PREFIX = "--";

        /**
         * The output stream to write to.
         */
        private DataOutputStream out = null;

        /**
         * The multipart boundary string.
         */
        private String boundary = null;

        /**
         * Creates a new <code>MultiPartFormOutputStream</code> object using
         * the specified output stream and boundary.  The boundary is required
         * to be created before using this method, as described in the
         * description for the <code>getContentType(String)</code> method.
         * The boundary is only checked for <code>null</code> or empty string,
         * but it is recommended to be at least 6 characters.  (Or use the
         * static createBoundary() method to create one.)
         *
         * @param os       the output stream
         * @param boundary the boundary
         *
         * @see #createBoundary()
         * @see #getContentType(String)
         */
        public MultipartOutputStream(OutputStream os, String boundary) {
            if (os == null) {
                throw new IllegalArgumentException("Output stream is required.");
            }
            if (boundary == null || boundary.length() == 0) {
                throw new IllegalArgumentException("Boundary stream is required.");
            }
            this.out = new DataOutputStream(os);
            this.boundary = boundary;
        }

        /**
         * Creates a multipart boundary string by concatenating 20 hyphens (-)
         * and the hexadecimal (base-16) representation of the current time in
         * milliseconds.
         *
         * @return a multipart boundary string
         *
         * @see #getContentType(String)
         */
        public static String createBoundary() {
            return "--------------------" +
                    Long.toString(System.currentTimeMillis(), 16);
        }

        /**
         * Gets the content type string suitable for the
         * <code>java.net.URLConnection</code> which includes the multipart
         * boundary string.  <br/>
         * <br/>
         * This method is static because, due to the nature of the
         * <code>java.net.URLConnection</code> class, once the output stream
         * for the connection is acquired, it's too late to set the content
         * type (or any other request parameter).  So one has to create a
         * multipart boundary string first before using this class, such as
         * with the <code>createBoundary()</code> method.
         *
         * @param boundary the boundary string
         *
         * @return the content type string
         *
         * @see #createBoundary()
         */
        public static String getContentType(String boundary) {
            return "multipart/form-data; boundary=" + boundary;
        }

        /**
         * Writes an boolean field value.
         *
         * @param name  the field name (required)
         * @param value the field value
         *
         * @throws IOException on input/output errors
         */
        public void writeField(String name, boolean value)
                throws IOException {
            writeField(name, new Boolean(value).toString());
        }

        /**
         * Writes an double field value.
         *
         * @param name  the field name (required)
         * @param value the field value
         *
         * @throws IOException on input/output errors
         */
        public void writeField(String name, double value)
                throws IOException {
            writeField(name, Double.toString(value));
        }

        /**
         * Writes an float field value.
         *
         * @param name  the field name (required)
         * @param value the field value
         *
         * @throws IOException on input/output errors
         */
        public void writeField(String name, float value)
                throws IOException {
            writeField(name, Float.toString(value));
        }

        /**
         * Writes an long field value.
         *
         * @param name  the field name (required)
         * @param value the field value
         *
         * @throws IOException on input/output errors
         */
        public void writeField(String name, long value)
                throws IOException {
            writeField(name, Long.toString(value));
        }

        /**
         * Writes an int field value.
         *
         * @param name  the field name (required)
         * @param value the field value
         *
         * @throws IOException on input/output errors
         */
        public void writeField(String name, int value)
                throws IOException {
            writeField(name, Integer.toString(value));
        }

        /**
         * Writes an short field value.
         *
         * @param name  the field name (required)
         * @param value the field value
         *
         * @throws IOException on input/output errors
         */
        public void writeField(String name, short value)
                throws IOException {
            writeField(name, Short.toString(value));
        }

        /**
         * Writes an char field value.
         *
         * @param name  the field name (required)
         * @param value the field value
         *
         * @throws IOException on input/output errors
         */
        public void writeField(String name, char value)
                throws IOException {
            writeField(name, new Character(value).toString());
        }

        /**
         * Writes an string field value.  If the value is null, an empty string
         * is sent ("").
         *
         * @param name  the field name (required)
         * @param value the field value
         *
         * @throws IOException on input/output errors
         */
        public void writeField(String name, String value)
                throws IOException {
            if (name == null) {
                throw new IllegalArgumentException("Name cannot be null or empty.");
            }
            if (value == null) {
                value = "";
            }
        /*
          --boundary\r\n
          Content-Disposition: form-data; name="<fieldName>"\r\n
          \r\n
          <value>\r\n
          */
            // write boundary
            out.writeBytes(PREFIX);
            out.writeBytes(boundary);
            out.writeBytes(NEWLINE);
            // write content header
            out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
            out.writeBytes(NEWLINE);
            out.writeBytes(NEWLINE);
            // write content
            out.writeBytes(value);
            out.writeBytes(NEWLINE);
            out.flush();
        }

        /**
         * Writes a file's contents.  If the file is null, does not exists, or
         * is a directory, a <code>java.lang.IllegalArgumentException</code>
         * will be thrown.
         *
         * @param name     the field name
         * @param mimeType the file content type (optional, recommended)
         * @param file     the file (the file must exist)
         *
         * @throws IOException on input/output errors
         */
        public void writeFile(String name, String mimeType, File file)
                throws IOException {
            if (file == null) {
                throw new IllegalArgumentException("File cannot be null.");
            }
            if (!file.exists()) {
                throw new IllegalArgumentException("File does not exist.");
            }
            if (file.isDirectory()) {
                throw new IllegalArgumentException("File cannot be a directory.");
            }
            writeFile(name, mimeType, file.getCanonicalPath(), new FileInputStream(file));
        }

        /**
         * Writes a input stream's contents.  If the input stream is null, a
         * <code>java.lang.IllegalArgumentException</code> will be thrown.
         *
         * @param name     the field name
         * @param mimeType the file content type (optional, recommended)
         * @param fileName the file name (required)
         * @param is       the input stream
         *
         * @throws IOException on input/output errors
         */
        public void writeFile(String name, String mimeType,
                              String fileName, InputStream is)
                throws IOException {
            if (is == null) {
                throw new IllegalArgumentException("Input stream cannot be null.");
            }
            if (fileName == null || fileName.length() == 0) {
                throw new IllegalArgumentException("File name cannot be null or empty.");
            }
        /*
          --boundary\r\n
          Content-Disposition: form-data; name="<fieldName>"; filename="<filename>"\r\n
          Content-Type: <mime-type>\r\n
          \r\n
          <file-data>\r\n
          */
            // write boundary
            out.writeBytes(PREFIX);
            out.writeBytes(boundary);
            out.writeBytes(NEWLINE);
            // write content header
            out.writeBytes("Content-Disposition: form-data; name=\"" + name +
                    "\"; filename=\"" + fileName + "\"");
            out.writeBytes(NEWLINE);
            if (mimeType != null) {
                out.writeBytes("Content-Type: " + mimeType);
                out.writeBytes(NEWLINE);
            }
            out.writeBytes(NEWLINE);
            // write content
            byte[] data = new byte[1024];
            int r = 0;
            while ((r = is.read(data, 0, data.length)) != -1) {
                out.write(data, 0, r);
            }
            // close input stream, but ignore any possible exception for it
            try {
                is.close();
            } catch (Exception e) {
            }
            out.writeBytes(NEWLINE);
            out.flush();
        }

        /**
         * Writes the given bytes.  The bytes are assumed to be the contents
         * of a file, and will be sent as such.  If the data is null, a
         * <code>java.lang.IllegalArgumentException</code> will be thrown.
         *
         * @param name     the field name
         * @param mimeType the file content type (optional, recommended)
         * @param fileName the file name (required)
         * @param data     the file data
         *
         * @throws IOException on input/output errors
         */
        public void writeFile(String name, String mimeType,
                              String fileName, byte[] data)
                throws IOException {
            if (data == null) {
                throw new IllegalArgumentException("Data cannot be null.");
            }
            if (fileName == null || fileName.length() == 0) {
                throw new IllegalArgumentException("File name cannot be null or empty.");
            }
        /*
          --boundary\r\n
          Content-Disposition: form-data; name="<fieldName>"; filename="<filename>"\r\n
          Content-Type: <mime-type>\r\n
          \r\n
          <file-data>\r\n
          */
            // write boundary
            out.writeBytes(PREFIX);
            out.writeBytes(boundary);
            out.writeBytes(NEWLINE);
            // write content header
            out.writeBytes("Content-Disposition: form-data; name=\"" + name +
                    "\"; filename=\"" + fileName + "\"");
            out.writeBytes(NEWLINE);
            if (mimeType != null) {
                out.writeBytes("Content-Type: " + mimeType);
                out.writeBytes(NEWLINE);
            }
            out.writeBytes(NEWLINE);
            // write content
            out.write(data, 0, data.length);
            out.writeBytes(NEWLINE);
            out.flush();
        }

        /**
         * Closes the stream.  <br/>
         * <br/>
         * <b>NOTE:</b> This method <b>MUST</b> be called to finalize the
         * multipart stream.
         *
         * @throws IOException on input/output errors
         */
        public void close() throws IOException {
            // write final boundary
            out.writeBytes(PREFIX);
            out.writeBytes(boundary);
            out.writeBytes(PREFIX);
            out.writeBytes(NEWLINE);
            out.flush();
            out.close();
        }

        /**
         * Gets the multipart boundary string being used by this stream.
         *
         * @return the boundary
         */
        public String getBoundary() {
            return this.boundary;
        }
    }
}

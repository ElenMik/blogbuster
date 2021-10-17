package net.ausiasmarch.blogbuster2021;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Random;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

    class LocalDateAdapter implements JsonSerializer<LocalDate> {
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
    }
}

public class Control extends HttpServlet {

    Properties properties = new Properties();
    HikariConnection oConnectionPool = null;

    private void opDelay(Integer iLast) {
        try {
            Thread.sleep(iLast);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void loadResourceProperties() throws FileNotFoundException, IOException {
        // https://stackoverflow.com/questions/44499306/how-to-read-application-properties-file-without-environment?noredirect=1&lq=1
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try ( InputStream resourceStream = loader.getResourceAsStream("application.properties")) {
            properties.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws ServletException {
        // https://stackoverflow.com/questions/13638978/java-servlets-overriding-initservletconfig-config
        try {
            loadResourceProperties();
            Class.forName("com.mysql.jdbc.Driver");
            oConnectionPool = new HikariConnection(
                    getConnectionChain(properties.getProperty("database.host"), properties.getProperty("database.port"), properties.getProperty("database.dbname")),
                    properties.getProperty("database.username"),
                    properties.getProperty("database.password"),
                    Integer.parseInt(properties.getProperty("databaseMinPoolSize")),
                    Integer.parseInt(properties.getProperty("databaseMaxPoolSize"))
            );

        } catch (ClassNotFoundException | IOException ex) {
            System.out.print("ERROR");
        }
    }

    private void doCORS(HttpServletRequest oRequest, HttpServletResponse oResponse) {
        oResponse.setContentType("application/json;charset=UTF-8");
        if (!(oRequest.getMethod().equalsIgnoreCase("OPTIONS"))) {
            oResponse.setHeader("Cache-control", "no-cache, no-store");
            oResponse.setHeader("Pragma", "no-cache");
            oResponse.setHeader("Expires", "-1");
            oResponse.setHeader("Access-Control-Allow-Origin", oRequest.getHeader("origin"));
            oResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD,PATCH");
            oResponse.setHeader("Access-Control-Max-Age", "86400");
            oResponse.setHeader("Access-Control-Allow-Credentials", "true");
            oResponse.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, "
                    + "Origin, "
                    + "Accept, "
                    + "Authorization, "
                    + "ResponseType, "
                    + "Observe, "
                    + "X-Requested-With, "
                    + "Content-Type, "
                    + "Access-Control-Expose-Headers, "
                    + "Access-Control-Request-Method, "
                    + "Access-Control-Request-Headers");
        } else {
            // https://stackoverflow.com/questions/56479150/access-blocked-by-cors-policy-response-to-preflight-request-doesnt-pass-access
            System.out.println("Pre-flight");
            oResponse.setHeader("Access-Control-Allow-Origin", oRequest.getHeader("origin"));
            oResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD,PATCH");
            oResponse.setHeader("Access-Control-Max-Age", "3600");
            oResponse.setHeader("Access-Control-Allow-Credentials", "true");
            oResponse.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, "
                    + "Origin, "
                    + "Accept, "
                    + "Authorization, "
                    + "ResponseType, "
                    + "Observe, "
                    + "X-Requested-With, "
                    + "Content-Type, "
                    + "Access-Control-Expose-Headers, "
                    + "Access-Control-Request-Method, "
                    + "Access-Control-Request-Headers");
            oResponse.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private String getConnectionChain(String databaseHost, String databasePort, String databaseName) {
        return "jdbc:mysql://" + databaseHost + ":" + databasePort + "/"
                + databaseName + "?autoReconnect=true&useSSL=false";
    }

    private static String getBody(HttpServletRequest request) throws IOException {
        //https://stackoverflow.com/questions/14525982/getting-request-payload-from-post-request-in-java-servlet
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doCORS(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doCORS(request, response);
        String ob = request.getParameter("ob");
        String op = request.getParameter("op");
        Gson oGson = new Gson();
        try ( PrintWriter out = response.getWriter()) {
            if (("".equalsIgnoreCase(ob) && "".equalsIgnoreCase(op)) || (ob == null && op == null)) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                out.print(oGson.toJson("Method Not Allowed"));
            } else {
                HttpSession oSession = request.getSession();
                UserBean oUserBean1 = null;
                String name = null;
                switch (ob) {
                    case "session":
                        switch (op) {
                            case "check":
                                oUserBean1 = (UserBean) oSession.getAttribute("usuario");
                                if (oUserBean1 != null) {
                                    name = oUserBean1.getLogin();
                                    if (name != null) {
                                        response.setStatus(HttpServletResponse.SC_OK);
                                        out.print(oGson.toJson(name));
                                    } else {
                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                        out.print(oGson.toJson("Unauthorized")); 
                                    }                                    
                                } else {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    out.print(oGson.toJson("Unauthorized"));
                                }
                                break;
                            case "get":
                                oUserBean1 = (UserBean) oSession.getAttribute("usuario");
                                name = null;
                                if (oUserBean1 != null) {
                                    name = oUserBean1.getLogin();
                                }
                                if (name != null) {
                                    if (name.equalsIgnoreCase("admin")) {
                                        response.setStatus(HttpServletResponse.SC_OK);
                                        out.print(oGson.toJson("QWERTY"));
                                    } else {
                                        response.setStatus(HttpServletResponse.SC_OK);
                                        out.print(oGson.toJson("ASDFG"));
                                    }
                                } else {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    out.print(oGson.toJson("Unauthorized"));
                                }
                                break;
                            case "connect":
                                oUserBean1 = (UserBean) oSession.getAttribute("usuario");
                                name = null;
                                if (oUserBean1 != null) {
                                    name = oUserBean1.getLogin();
                                    if (name != null) {
                                        if (name.equalsIgnoreCase("admin")) {
                                            String dbversion = null;
                                            try ( Connection oConnection = oConnectionPool.newConnection()) {
                                                Statement stmt = oConnection.createStatement();
                                                ResultSet rs = stmt.executeQuery("SELECT version()");
                                                if (rs.next()) {
                                                    dbversion = "Database Version : " + rs.getString(1);
                                                } else {
                                                    throw new Exception("Error al obtener la versión de la base de datos");
                                                }
                                                //oConnection.close(); -> ver Mark
                                            } catch (Exception ex) {
                                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                                out.print(oGson.toJson(ex.getMessage()));
                                            }
                                            response.setStatus(HttpServletResponse.SC_OK);
                                            out.print(oGson.toJson(dbversion));
                                        }
                                    }
                                }
                                break;
                            default:
                                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                                out.print(oGson.toJson("Method Not Allowed"));
                                break;
                        }
                        break;
                    case "post":
                         switch (op) {
                            case "getone":
                                Integer id = Integer.parseInt(request.getParameter("id"));
                                try ( Connection oConnection = oConnectionPool.newConnection()) {
                                    PostDAO oPostDao = new PostDAO(oConnection);
                                    PostBean oPostBean = oPostDao.getOne(id);
                                    response.setStatus(HttpServletResponse.SC_OK);
                                    out.print(oGson.toJson(oPostBean));
                                } catch (Exception ex) {
                                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                    out.print(oGson.toJson(ex.getMessage()));
                                }
                                break;
                            case "getpage":
                                response.setStatus(HttpServletResponse.SC_OK);
                                out.print(oGson.toJson("post.getpage"));
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        out.print(oGson.toJson("Method Not Allowed"));
                        break;
                }

            }
        }
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doCORS(request, response);
        String ob = request.getParameter("ob");
        String op = request.getParameter("op");

        //https://stackoverflow.com/questions/22310143/java-8-localdatetime-deserialized-using-gson
        Gson oGson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() { 
            @Override 
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException { 
                return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")); 
            } 
        }).create();

        HttpSession oSession = request.getSession();
        try ( PrintWriter out = response.getWriter()) {
            if (("".equalsIgnoreCase(ob) && "".equalsIgnoreCase(op)) || (ob == null && op == null)) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                out.print(oGson.toJson("Method Not Allowed"));
            } else {
                switch (ob) {
                    case "session":
                        switch (op) {
                            case "login":
                                String payloadRequest = getBody(request);
                                UserBean oUserBean = new UserBean();

                                oUserBean = oGson.fromJson(payloadRequest, oUserBean.getClass());

                                if (oUserBean.getLogin() != null && oUserBean.getPassword() != null) {
                                    if (oUserBean.getLogin().equalsIgnoreCase("admin") && oUserBean.getPassword().equalsIgnoreCase("8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918")) { //admin
                                        oSession.setAttribute("usuario", oUserBean);
                                        response.setStatus(HttpServletResponse.SC_OK);
                                        out.print(oGson.toJson("Welcome"));
                                    } else {
                                        if (oUserBean.getLogin().equalsIgnoreCase("user") && oUserBean.getPassword().equalsIgnoreCase("04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb")) { //user
                                            oSession.setAttribute("usuario", oUserBean);
                                            response.setStatus(HttpServletResponse.SC_OK);
                                            out.print(oGson.toJson("Welcome"));
                                        } else {
                                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                            out.print(oGson.toJson("Auth Error"));
                                        }
                                    }
                                } else {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    out.print(oGson.toJson("Auth Error"));
                                }
                                break;
                            default:
                                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                                out.print(oGson.toJson("Method Not Allowed"));
                                break;
                        }
                        break;
                    case "post":
                        switch (op) {
                            case "create":
                                UserBean oUserBean = (UserBean) oSession.getAttribute("usuario");
                                String name = null;
                                if (oUserBean != null) {
                                    name = oUserBean.getLogin();
                                    if (name != null) {
                                        if (name.equalsIgnoreCase("admin")) {
                                            String payloadRequest = getBody(request);
                                            PostBean oPostBean = new PostBean();
                                            try {
                                                oPostBean = oGson.fromJson(payloadRequest, oPostBean.getClass());
                                                try (Connection oConnection = oConnectionPool.newConnection()) {
                                                    PostDAO oPostDao = new PostDAO(oConnection);
                                                    int iResult = oPostDao.create(oPostBean);
                                                    response.setStatus(HttpServletResponse.SC_OK);
                                                    out.print(oGson.toJson(iResult));
                                                } catch (Exception ex) {
                                                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                                    out.print(oGson.toJson(ex.getMessage()));
                                                }
                                            } catch (Exception ex){
                                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                                out.print(oGson.toJson(ex.getMessage()));
                                            }                                           
                                        }
                                    } else {
                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                        out.print(oGson.toJson("Unauthorized"));
                                    }
                                } else {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    out.print(oGson.toJson("Unauthorized"));
                                }
                                break;
                            default:
                                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                                out.print(oGson.toJson("Method Not Allowed"));
                                break;
                        }
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        out.print(oGson.toJson("Method Not Allowed"));
                        break;
                }
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doCORS(request, response);
        String ob = request.getParameter("ob");
        String op = request.getParameter("op");
        Gson oGson = new Gson();
        try ( PrintWriter out = response.getWriter()) {
            if (("".equalsIgnoreCase(ob) && "".equalsIgnoreCase(op)) || (ob == null && op == null)) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                out.print(oGson.toJson("Method Not Allowed"));
            } else {
                switch (ob) {
                    case "session":
                        switch (op) {
                            case "logout":
                                HttpSession oSession = request.getSession();
                                oSession.invalidate();
                                response.setStatus(HttpServletResponse.SC_OK);
                                out.print(oGson.toJson("Session closed"));
                            break;
                            default:
                                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                                out.print(oGson.toJson("Method Not Allowed"));
                                break;
                        }
                        break;
                    case "post":
                        switch (op) {
                            case "delete":
                                HttpSession oSession = request.getSession();
                                UserBean oUserBean = (UserBean) oSession.getAttribute("usuario");
                                String name = null;
                                if (oUserBean != null) {
                                    name = oUserBean.getLogin();
                                    if (name != null) {
                                        if (name.equalsIgnoreCase("admin")) {
                                            Integer id = Integer.parseInt(request.getParameter("id"));
                                            try ( Connection oConnection = oConnectionPool.newConnection()) {
                                                PostDAO oPostDao = new PostDAO(oConnection);
                                                PostBean oPostBean = oPostDao.getOne(id);
                                                response.setStatus(HttpServletResponse.SC_OK);
                                                out.print(oGson.toJson(oPostBean));
                                            } catch (Exception ex) {
                                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                                out.print(oGson.toJson(ex.getMessage()));
                                            }
                                        } else {
                                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                            out.print(oGson.toJson("Unauthorized"));
                                        }
                                    } else {
                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                        out.print(oGson.toJson("Unauthorized"));
                                    }
                                } else {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    out.print(oGson.toJson("Unauthorized"));
                                }
                                break;
                            default:
                                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                                out.print(oGson.toJson("Method Not Allowed"));
                                break;
                        }
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        out.print(oGson.toJson("Method Not Allowed"));
                        break;
                }
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doCORS(request, response);
        String ob = request.getParameter("ob");
        String op = request.getParameter("op");
        Gson oGson = new Gson();
        try ( PrintWriter out = response.getWriter()) {
            if (("".equalsIgnoreCase(ob) && "".equalsIgnoreCase(op)) || (ob == null && op == null)) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                out.print(oGson.toJson("Method Not Allowed"));
            } else {
                switch (ob) {
                    case "post":
                        switch (op) {
                            case "update":
                                String payloadRequest = getBody(request);
                                PostBean oPostBean = new PostBean();
                                oPostBean = oGson.fromJson(payloadRequest, PostBean.class);
                                try ( Connection oConnection = oConnectionPool.newConnection()) {
                                    PostDAO oPostDao = new PostDAO(oConnection);
                                    response.setStatus(HttpServletResponse.SC_OK);
                                    out.print(oGson.toJson(oPostBean));
                                    oPostDao.update(oPostBean);
                                    response.setStatus(HttpServletResponse.SC_OK);
                                    out.print(oGson.toJson("post.update"));
                                    } catch (Exception ex) {
                                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                    out.print(oGson.toJson(ex.getMessage()));
                                    }         
                                break;
                        }
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        out.print(oGson.toJson("Method Not Allowed"));
                        break;
                }
            }
        }
    }

    @Override
    public void destroy() {
        try {
            oConnectionPool.closePool();
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }
    }

}

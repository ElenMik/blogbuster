package net.ausiasmarch.blogbuster2021;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class PostDAO {

    private Connection oConnection = null;

    public PostDAO(Connection oConnection) {
        this.oConnection = oConnection;
    }

    private LocalDateTime convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    public PostBean getOne(int id) throws SQLException {
        String srtSQL = "SELECT * FROM post WHERE id=?";
        PreparedStatement oPreparedStatement = oConnection.prepareStatement(srtSQL);
        oPreparedStatement.setInt(1, id);
        ResultSet oResultSet = oPreparedStatement.executeQuery();
        PostBean oPostBean = null;
        if (oResultSet.next()) {
            oPostBean = new PostBean();
            oPostBean.setId(id);
            oPostBean.setTitulo(oResultSet.getString("titulo"));
            oPostBean.setCuerpo(oResultSet.getString("cuerpo"));
            oPostBean.setFecha(convertToLocalDateViaInstant(oResultSet.getDate("fecha")));
            oPostBean.setEtiquetas(oResultSet.getString("etiquetas"));
            oPostBean.setVisible(oResultSet.getBoolean("visible"));
        }
        oResultSet.close();
        oPreparedStatement.close();
        return oPostBean;
    }

    public int delete(int id) throws SQLException {
        String srtSQL = "DELETE FROM post WHERE id=?";
        PreparedStatement oPreparedStatement = oConnection.prepareStatement(srtSQL);
        oPreparedStatement.setInt(1, id);
        int result = oPreparedStatement.executeUpdate();
        oPreparedStatement.close();
        return result;
    }

    public int create(PostBean oPostBean) throws SQLException {
        String srtSQL = "INSERT INTO post (titulo,cuerpo,fecha,etiquetas,visible) VALUES (?,?,?,?,?)";
       
        PreparedStatement oPreparedStatement = oConnection.prepareStatement(srtSQL);
        oPreparedStatement.setString(1, oPostBean.getTitulo());
        oPreparedStatement.setString(2, oPostBean.getCuerpo());
        oPreparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(oPostBean.getFecha()));
        oPreparedStatement.setString(4, oPostBean.getEtiquetas());
        oPreparedStatement.setBoolean(5, oPostBean.getVisible());
       
        int result = oPreparedStatement.executeUpdate();
        oPreparedStatement.close();
        return result;
    }
    
    public void update(PostBean objeto) throws SQLException{
        String srtSQL = "UPDATE post SET titulo=?, cuerpo=?, fecha=?,etiquetas=?,visible=? WHERE id=?";

        PreparedStatement oPreparedStatement = oConnection.prepareStatement(srtSQL);
        oPreparedStatement.setString(1, objeto.getTitulo());
        oPreparedStatement.setString(2, objeto.getCuerpo());
        oPreparedStatement.setDate(3, java.sql.Date.valueOf(LocalDateTime.now().toLocalDate()));
        oPreparedStatement.setString(4, objeto.getEtiquetas());
        oPreparedStatement.setBoolean(5, objeto.getVisible());
        oPreparedStatement.setInt(6, objeto.getId());

        int result = oPreparedStatement.executeUpdate();
        oPreparedStatement.close();

    }

}

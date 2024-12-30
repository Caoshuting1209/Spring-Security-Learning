package com.shuting.springsecurityoauth2.commons.config.typeHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MapTypeHandler extends BaseTypeHandler<HashMap<String, String>> {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void setNonNullParameter(
      PreparedStatement ps, int i, HashMap<String, String> parameter, JdbcType jdbcType)
      throws SQLException {
    if (parameter == null) {
      ps.setString(i, null);
    }
    if (parameter != null) {
      try {
        ps.setString(i, objectMapper.writeValueAsString(parameter));
      } catch (JsonProcessingException e) {
        throw new SQLException("Error converting parameter list to JSON string", e);
      }
    }
  }

  @Override
  public HashMap<String, String> getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    String json = rs.getString(columnName);
    return parseJson(json);
  }

  @Override
  public HashMap<String, String> getNullableResult(ResultSet rs, int columnIndex)
      throws SQLException {
    String json = rs.getString(columnIndex);
    return parseJson(json);
  }

  @Override
  public HashMap<String, String> getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    String json = cs.getString(columnIndex);
    return parseJson(json);
  }

  private HashMap<String, String> parseJson(String json) throws SQLException {
    try {
      if (json != null && !json.isEmpty()) {
        return objectMapper.readValue(json, new TypeReference<>() {});
      }
      return null;
    } catch (JsonProcessingException e) {
      throw new SQLException("Error parsing JSON string to parameter list", e);
    }
  }
}

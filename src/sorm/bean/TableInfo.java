package sorm.bean;

import java.util.List;
import java.util.Map;

/**
 * 表信息Javabean类
 */
public class TableInfo {
    /**
     * 表名
     */
    private String tableName;
    /**
     *  列名 列信息 映射集合
     */
    private Map<String,ColumnInfo> columns;
    /**
     * 唯一主键 一个列信息
     */
    private  ColumnInfo onlyKey;
    /**
     * 存放多个主键 只能操作一个主键的情况，多个会默认把靠前的当做唯一主键处理
     * 这里只是暂时存放，本次未用到
     */
    private List<ColumnInfo> priKeys;

    public TableInfo() {
    }

    public TableInfo(String tableName, Map<String, ColumnInfo> columns, List<ColumnInfo> priKeys) {
        this.tableName = tableName;
        this.columns = columns;
        this.priKeys = priKeys;
    }

    public List<ColumnInfo> getPriKeys() {
        return priKeys;
    }

    public void setPriKeys(List<ColumnInfo> priKeys) {
        this.priKeys = priKeys;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, ColumnInfo> columns) {
        this.columns = columns;
    }

    public ColumnInfo getOnlyKey() {
        return onlyKey;
    }

    public void setOnlyKey(ColumnInfo onlyKey) {
        this.onlyKey = onlyKey;
    }
}

package sorm.bean;

/**
 * 列信息Javabean
 *
 */
public class ColumnInfo {
    /**
     * 列名 属性名
     */
    private String name;
    /**
     * 属性的数据类型
     */
    private String dataType;
    /**
     * 键类型 0 1 2 普 主 外
     */
    private int keyType;

    public ColumnInfo() {
    }

    public ColumnInfo(String name, String dataType, int keyType) {
        this.name = name;
        this.dataType = dataType;
        this.keyType = keyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }
}

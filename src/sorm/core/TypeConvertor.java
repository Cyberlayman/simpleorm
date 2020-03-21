package sorm.core;

/**
 * 类型转换接口
 */
public interface TypeConvertor {
    /**
     *
     * @param dbType
     * @return
     */
    String dbType2javaType(String dbType);

    /**
     *
     * @param javaType
     * @return
     */
    String javaType2dbType(String javaType);
}

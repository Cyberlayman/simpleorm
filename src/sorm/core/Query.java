package sorm.core;

import java.util.List;

/**
 * 负责查询（核心服务类）
 *
 */
public interface Query {
    /**
     * 执行一个DML语句
     * @param sql  执行的sql语句
     * @param params  参数
     * @return   影响的数据行数
     */
    int executeDML(String sql, Object[] params);

    /**
     * 插入一个对象
     * @param o
     */
    void insert(Object o);

    /**
     * 删除clazz表示的类对应的表中主键id对应的数据
     * @param clazz   相应类的反射
     * @param priKey     要删除数据的主键值
     * @return   影响的行数
     */
    int delete(Class clazz, Object priKey);

    /**
     * 删除一个对象
     * @param o 表信息反射的类的实例
     */
    void delete(Object o);

    /**
     * 更新数据 参数示例 (r1,new String[]{"uname","regTime"})
     * @param o  更新的对象（行）  此对象是包含 新值 的对象
     * @param propNames   对象需要更改的属性名称
     * @return
     */
    int update(Object o, String[] propNames);

    /**
     * 更新除了 主键属性 之外的所有属性 传入的对象 其所有属性都要有值否则可能报错或者更新为空
     * @param o  表对应的类对象实例  （表中的一行）
     * @return
     */
    int update(Object o);

    /**
     * 查询多行
     * @param sql
     * @param clazz  封装到clazz中  表对应的类名.class
     * @param params  sql的参数
     * @return   返回结果
     */
    List queryRows(String sql, Class clazz, Object[] params);

    /**
     * 表名加上  主键约束 数组最多两个值不能为空  下界放在0，上界放一
     * 只有一个默认为下界
     * @param clazz
     * @param limits
     * @return
     */
    List queryRows(Class clazz, Object[] limits);

    /**
     * 查询单行
     * @param sql
     * @param clazz
     * @param params
     * @return
     */
    Object queryRow(String sql, Class clazz, Object[] params);

    /**
     * 表名加上主键的值 获得这一行信息
     * @param clazz
     * @param keyValue
     * @return
     */
    Object queryRow(Class clazz, Object keyValue);

    /**
     * 查询单个值 即一行一列   根据主键的值查询其他属性的值
     * select ? from tableName where priKey=?
     * @param FieldName  要查询属性的名字
     * @param tableName    表名
     * @param priKey     主键的值
     * @return
     */
    Object queryValue(String FieldName, String tableName, Object priKey);

    /**
     * 查询数字类型的单个数据
     * @param FieldName   要查询数字类属性的名字
     * @param tableName   表名
     * @param priKey  主键的值
     * @return
     */
    Number queryNumber(String FieldName, String tableName, Object priKey);

}

package sorm.bean;

/**
 * 以拼接字符串的方式 生成Javabean 的类代码
 * 声明属性语句
 * get set方法语句 但没有 包类的声明
 *
 */
public class JavabeanFileSrc {
    /**
     * 属性声明语句
     */
    private String attributesLanguage;
    /**
     * set方法语句
     */
    private String setMethodLanguage;
    /**
     * get方法语句
     */
    private String getMethodLanguage;

    public JavabeanFileSrc() {
    }

    public String getAttributesLanguage() {
        return attributesLanguage;
    }

    public void setAttributesLanguage(String attributesLanguage) {
        this.attributesLanguage = attributesLanguage;
    }

    public String getSetMethodLanguage() {
        return setMethodLanguage;
    }

    public void setSetMethodLanguage(String setMethodLanguage) {
        this.setMethodLanguage = setMethodLanguage;
    }

    public String getGetMethodLanguage() {
        return getMethodLanguage;
    }

    public void setGetMethodLanguage(String getMethodLanguage) {
        this.getMethodLanguage = getMethodLanguage;
    }

    /**
     * 符合代码输出的 toString方法
     * @return
     */
    @Override
    public String toString() {
        return attributesLanguage+setMethodLanguage+getMethodLanguage;
    }
}

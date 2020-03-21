package sorm.core;

/**
 *
 * 单例模式的Query工厂类
 */
public class QueryFactory {
    private volatile static QueryFactory q = null;

    private QueryFactory(){
        if(q!=null){
            throw new RuntimeException("此类为单例模式，已经有实例存在，不能重复创建");
        }
    }
    public static QueryFactory getInstance(){
        if(q==null){
            synchronized (QueryFactory.class){
                if(q==null){
                    q = new QueryFactory();
                }
            }
        }
        return q;
    }

    public MysqlQuery createMysqlQuery(){

        return new MysqlQuery();
    }

}

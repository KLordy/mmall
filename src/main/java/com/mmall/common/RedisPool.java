package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool pool;
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));;//最大的空闲状态的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","20"));

    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));//表示每次从pool里获取实例前，会有一个简单的测试例子，例子成功才获取实例；如果设置为true，则得到的jedis实例肯定是可以用的。
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","false"));//表示每次往pool里返还实例前，会有一个简单的测试例子，例子成功才返还实例；如果设置为true，则返还到pool的jedis实例肯定是可以用的。

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");//表示每次从pool里获取实例前，会有一个简单的测试例子，例子成功才获取实例；如果设置为true，则得到的jedis实例肯定是可以用的。
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));//表示每次往pool里返还实例前，会有一个简单的测试例子，例子成功才返还实例；如果设置为true，则返还到pool的jedis实例肯定是可以用的。

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽时，新来的请求是否阻塞，false会抛出异常，true则会阻塞直到超时，默认为true。

        pool = new JedisPool(config,redisIp,redisPort,1000*2);
    }

    static{
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("hello","111");
        returnResource(jedis);

        pool.destroy();//临时调用，实际生产环境不会调用，销毁连接池中的所有连接
        System.out.println("ends!");
    }
}

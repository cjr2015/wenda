package com.nowcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;

    public static void print(int index, Object obj) {
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://192.168.243.131:6379/10");
//        jedis.flushDB();
//        jedis.set("hello","world");
//        print(1,jedis.get("hello"));
//        jedis.rename("hello","newhello");
//        print(2,jedis.get("newhello"));
//        jedis.setex("hello2",18,"world2");
//        jedis.set("pv","100");
//        jedis.incr("pv");
//        print(3,jedis.get("pv"));
//        jedis.incrBy("pv",4);
//        print(4,jedis.get("pv"));
//        String listName="list";
//        jedis.del(listName);
//        for(int i=0;i<10;i++){
//            jedis.lpush(listName,"a"+String.valueOf(i));
//        }
//        print(5,jedis.lrange(listName,0,5));
//        print(6,jedis.llen(listName));
//        print(7,jedis.lpop(listName));
//        print(8,jedis.llen(listName));
//        print(5,jedis.lrange(listName,0,10));
//        print(9,jedis.lindex(listName,1));
//        print(9,jedis.lpush(listName,"67"));
//        print(10,jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","1234"));
//        print(5,jedis.lrange(listName,0,10));
//
//        String userkey="userxx";
//        jedis.hset(userkey,"name","jim");
//        jedis.hset(userkey,"age","12");
//        jedis.hset(userkey,"phon","6767");
//        print(11,jedis.hget(userkey,"name"));
//        print(12,jedis.hgetAll(userkey));
//        print(13,jedis.hexists(userkey,"email"));
//        print(14,jedis.hkeys(userkey));
//        print(14,jedis.hvals(userkey));
//        jedis.hsetnx(userkey,"school","ljl");
//        jedis.hsetnx(userkey,"name","yxy");
//        print(15,jedis.hgetAll(userkey));
//
//        String likekey1 = "commentLike1";
//        String likekey2 = "commentLike2";
//        for(int i=0;i<10;i++){
//            jedis.sadd(likekey1,String.valueOf(i));
//            jedis.sadd(likekey2,String.valueOf(i*i));
//        }
//        print(16,jedis.smembers(likekey1));
//        print(16,jedis.smembers(likekey2));
//        print(17,jedis.sunion(likekey1,likekey2));
//        print(18,jedis.sdiff(likekey1,likekey2));
//        print(19,jedis.sinter(likekey1,likekey2));
//        print(20,jedis.sismember(likekey1,"9"));
//        jedis.srem(likekey1,"5");
//        print(21,jedis.smembers(likekey1));
//        jedis.smove(likekey2,likekey1,"25");
//        print(21,jedis.smembers(likekey1));
//        print(21,jedis.smembers(likekey2));
//        print(22,jedis.scard(likekey2));
//
//        String setKey = "zset";
//        jedis.zadd(setKey,1,"a");
//        jedis.zadd(setKey,1,"b");
//        jedis.zadd(setKey,1,"c");
//        jedis.zadd(setKey,2,"d");
//        jedis.zadd(setKey,1,"e");
//        print(23,jedis.zlexcount(setKey,"-","+"));
//        print(24,jedis.zlexcount(setKey,"[b","[d"));
//        print(25,jedis.zrange(setKey,0,10));
//        jedis.zremrangeByLex(setKey,"(c","+");
//        print(25,jedis.zrange(setKey,0,10));
//        jedis.lpush("abc","1234");
//        jedis.lpush("abc","5678");
//        print(26,jedis.brpop(0,"abc"));
//        print(26,jedis.brpop(0,"abc"));
//
//           print(1, jedis.setnx("a","10086"));

        System.out.println(jedis.get("c"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://192.168.243.131:6379/10");
    }
    public  Long setnx(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.setnx(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0l;
    }
    public String get(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.get(key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }
    public long expire(String key,int seconds){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.expire(key,seconds);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }
    public  long sadd(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
           return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }
    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
    public long llen(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.llen(key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
    public Jedis getJedis() {
        return pool.getResource();
    }

    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
        }
        return null;
    }

    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException ioe) {
                    // ..
                }
            }

            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
    public long zadd(String key,double score,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key,score,value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Double zscore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
    public List<String> lrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}

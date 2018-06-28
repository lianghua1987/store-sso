package com.hua.store.sso.dao.impl;

import com.hua.store.sso.dao.JedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisClientStandalone implements JedisClient {

    @Autowired
    private JedisPool pool;

    @Override
    public String get(String key) {
        Jedis jedis = pool.getResource();
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    @Override
    public String set(String key, String value) {
        Jedis jedis = pool.getResource();
        String result = jedis.set(key, value);
        jedis.close();
        return result;
    }

    @Override
    public long hset(String hkey, String key, String value) {
        Jedis jedis = pool.getResource();
        long result = jedis.hset(hkey, key, value);
        jedis.close();
        return result;
    }

    @Override
    public String hget(String hkey, String key) {
        Jedis jedis = pool.getResource();
        String value = jedis.hget(hkey, key);
        jedis.close();
        return value;
    }

    @Override
    public long incr(String key) {
        Jedis jedis = pool.getResource();
        long value = jedis.incr(key);
        jedis.close();
        return value;
    }

    @Override
    public long expire(String key, int seconds) {
        Jedis jedis = pool.getResource();
        long value = jedis.expire(key, seconds);
        jedis.close();
        return value;
    }

    @Override
    public long ttl(String key) {
        Jedis jedis = pool.getResource();
        long value = jedis.ttl(key);
        jedis.close();
        return value;
    }

    @Override
    public long del(String key) {
        Jedis jedis = pool.getResource();
        long value = jedis.del(key);
        jedis.close();
        return value;
    }

    @Override
    public long hdel(String hkey, String key) {
        Jedis jedis = pool.getResource();
        long value = jedis.hdel(hkey, key);
        jedis.close();
        return value;
    }
}

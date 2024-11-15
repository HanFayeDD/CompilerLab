package cn.edu.hitsz.compiler.asm;

import java.util.HashMap;
import java.util.Map;

public class BMap<K, V> {
    private final Map <K, V> kvmap = new HashMap<>();
    private final Map <V, K> vkmap = new HashMap<>();

    //获取
    public V getV(K key) {
        return kvmap.get(key);
    }
    public K getK(V value) {
        return vkmap.get(value);
    }

    //添加
    public void put(K key, V value) {
        kvmap.put(key, value);
        vkmap.put(value, key);
    }

    //检验
    public boolean containsK(K key) {
        return kvmap.containsKey(key);
    }

    public boolean containsV(V value) {
        return vkmap.containsKey(value);
    }


    //删除
    public void removebyK(K key) {
        if(!(kvmap.containsKey(key))){
            throw new RuntimeException("key not exist in kvmap");
        }
        V value = kvmap.get(key);
        kvmap.remove(key);
        vkmap.remove(value);
    }

    public void removebyV(V value) {
        if(!(vkmap.containsKey(value))){
            throw new RuntimeException("key not exist in vkmap");
        }
        K key = vkmap.get(value);
        kvmap.remove(key);
        vkmap.remove(value);
    }
}


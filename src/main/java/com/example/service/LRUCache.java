package com.example.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LRUCache<K, V> {

  private ConcurrentLinkedQueue<K> accessOrderLinkedQueue = new ConcurrentLinkedQueue<K>();

  private ConcurrentHashMap<K, V> kvConcurrentHashMap = new ConcurrentHashMap<K, V>();

  private int lruSize = 0;

  public LRUCache(final int size) {
    this.lruSize = size;
  }

  public K add(K key, V value) {
    if (kvConcurrentHashMap.containsKey(key)) {
      accessOrderLinkedQueue.remove(key);
    }
    while (accessOrderLinkedQueue.size() >= lruSize) {
      kvConcurrentHashMap.remove(accessOrderLinkedQueue.poll());
    }
    accessOrderLinkedQueue.add(key);
    kvConcurrentHashMap.put(key, value);

    return key;
  }

  public V get(K key) {
    V v = null;
    if (kvConcurrentHashMap.containsKey(key)) {
      accessOrderLinkedQueue.remove(key);
      v = kvConcurrentHashMap.get(key);
      accessOrderLinkedQueue.add(key);
    }
    return v;
  }

  public V remove(K key) {
    V v = null;
    if (kvConcurrentHashMap.contains(key)) {
      v = kvConcurrentHashMap.remove(key);
      accessOrderLinkedQueue.remove(key);
    }
    return v;
  }

  public int size() {
    return kvConcurrentHashMap.size();
  }
}

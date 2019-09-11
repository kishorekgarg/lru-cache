package com.example.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class LRUCacheTest {

  private LRUCache<Integer, String> lruCache;

  @Before
  public void init() {
    lruCache = new LRUCache<>(5);
    lruCache.add(1, "one");
    lruCache.add(2, "two");
    lruCache.add(3, "three");
    lruCache.add(4, "four");
    lruCache.add(5, "five");
  }

  @Test
  public void testAddCache() {
    lruCache.add(6, "six");
    assertNull("Object should be null", lruCache.get(1));
    assertEquals("LRU cache size should be 5", lruCache.size(), 5);
    lruCache.get(2);
    lruCache.add(7, "seven");
    assertNotNull("Object should not be null", lruCache.get(2));
  }

  @Test
  public void testRemoveCache() {
    lruCache.remove(7);
    assertNull("Object should be null", lruCache.get(7));
  }

  @Test
  public void testGetCache() {
    assertNotNull("Object should not be null", lruCache.get(1));
    assertNotNull("Object should not be null", lruCache.get(2));
    assertNotNull("Object should not be null", lruCache.get(3));
    assertNotNull("Object should not be null", lruCache.get(4));
    assertNotNull("Object should not be null", lruCache.get(5));
  }

  @Test
  public void testAddConcurrent() {
    int poolSize = 2000;
    ExecutorService service = Executors.newFixedThreadPool(poolSize);
    CountDownLatch latch = new CountDownLatch(1);
    Collection<Future<Integer>> futures = new ArrayList<>(poolSize);
    for (int thread = 0; thread < poolSize; thread++) {
      final String threadValue = String.format("Thread #%d", thread);
      final int finalThread = thread;
      futures.add(
          service.submit(
              () -> {
                latch.await();
                return lruCache.add(finalThread, threadValue);
              }
          )
      );
    }
    latch.countDown();
    Set<Integer> ids = new HashSet<>();
    for (Future<Integer> f : futures) {
      try {
        ids.add(f.get());
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    assertEquals("Total inserts are equal to poolSize", ids.size(), poolSize);
    assertEquals("LRU Cache size is 5", lruCache.size(), 5);
  }
}
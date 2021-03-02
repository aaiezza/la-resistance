package org.resistance.site.utils;

import java.util.List;

public class RandomPicker {
  public static synchronized <T> T pick(T[] items) {
    int choice = (int) Math.floor(Math.random() * (items.length));
    return items[choice];
  }

  public static synchronized <T> T pick(List<T> items) {
    int choice = (int) Math.floor(Math.random() * (items.size()));
    return items.get(choice);
  }
}

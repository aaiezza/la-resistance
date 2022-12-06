package org.resistance.site.mech;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.resistance.site.utils.RandomPicker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AINamer {
  private final String[] AI_names;

//  @Autowired
//  private AINamer(@Value("${AI.names}") String aiNames) {
  private AINamer() {
//    AI_names = aiNames.split(":");
    AI_names = "Lester Bester:Sir Freud:Madam Jewswelle:Jimmy the Hammer:Barry Cade:Bill Board:Jo King:Polly Ester:Phil Graves:May Flower:Lou Zar"
        .split(":");
  }

  public String getName() {
    return RandomPicker.pick(AI_names);
  }

  public Stack<String> getNames(int numNames) {
    Assert.isTrue(numNames >= 0 && numNames <= AI_names.length, String.format("Can't produce %d names", numNames));

    List<String> names = Arrays.asList(AI_names);

    Collections.shuffle(names);

    Stack<String> aiNames = new Stack<String>();

    aiNames.addAll(names.subList(0, numNames));

    return aiNames;
  }
}

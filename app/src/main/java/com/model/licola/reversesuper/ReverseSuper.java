package com.model.licola.reversesuper;

import java.io.IOException;
import reversesuper.generator.ReverseGenerator;

/**
 * Created by LiCola on 2018/6/6.
 */
public class ReverseSuper {

  public static void main(String[] args) throws IOException {
    ReverseGenerator.main(new Class[]{AccountManagerImpl.class}, "");
  }
}

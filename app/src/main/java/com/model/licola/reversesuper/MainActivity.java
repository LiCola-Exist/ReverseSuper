package com.model.licola.reversesuper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ModelManager impl = new ModelManagerImpl();

    Log.d("MainActivity", impl.reverseMethod("调用方法"));
  }
}

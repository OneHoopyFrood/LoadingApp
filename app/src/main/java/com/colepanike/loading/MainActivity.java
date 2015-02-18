package com.colepanike.loading;

import android.content.Context;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observable;


public class MainActivity extends ActionBarActivity implements LoadListener {

    private static Context context;

    private Button create;
    private Button load;
    private Button clear;

    private ProgressBar progress;

    private ListView numbersListView;

    Thread worker;
    Loader loader;

    private ArrayAdapter<Integer> adapter;
    private ArrayList<Integer> numbers = new ArrayList<Integer>();

    private final String filename = "numbers.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        create = (Button) findViewById(R.id.create_btn);
        load = (Button) findViewById(R.id.load_btn);
        clear = (Button) findViewById(R.id.clear_btn);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(10);

        adapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_list_item_1,
                numbers);

        numbersListView = (ListView) findViewById(R.id.numbers);

        numbersListView.setAdapter(adapter);

        loader = new Loader(context.getFilesDir().toString(), filename);
        loader.addListener(this);

        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(worker != null && worker.isAlive()) {
                    Toast.makeText(context, "Please wait for other operation", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "Creating file...", Toast.LENGTH_SHORT).show();
                worker = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(context.getFilesDir(), filename);
                        try {
                            FileWriter writer = new FileWriter(file);

                            for (Integer i = 1; i <= 10; i++){
                                writer.append(i.toString() + "\n");
                                Thread.sleep(250);
                            }
                            writer.close();
                        }
                        catch (IOException ex) {
                            Log.e("Creating IOException", ex.getMessage());
                        }
                        catch (InterruptedException ex) {
                            Log.e("Creating IOException", ex.getMessage());
                        }
                    }
                });
                worker.start();
            }
        });

        /**
         *
         */
        load.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(worker != null && worker.isAlive()) {
                    Toast.makeText(context, "Please wait for other operation", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "Loading file...", Toast.LENGTH_SHORT).show();
                worker = new Thread(loader);
                worker.start();
                Toast.makeText(context, "Check", Toast.LENGTH_SHORT).show();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adapter.clear();
            }
        });
    }

    @Override
    public void NumLoaded(final int num) {
        numbers.add(num);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                progress.setProgress(num);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

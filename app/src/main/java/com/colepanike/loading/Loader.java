package com.colepanike.loading;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by cole on 2/17/15.
 */
public class Loader implements Runnable {

    String filename;
    String filepath;

    private List<LoadListener> listeners = new ArrayList<LoadListener>();

    Loader(String filepath, String filename) {
        this.filename = filename;
        this.filepath = filepath;
    }

    Loader(String filepath, String filename, LoadListener listener) {
        this.filename = filename;
        this.filepath = filepath;
        addListener(listener);
    }

    public void addListener(LoadListener toAdd){
        listeners.add(toAdd);
    }

    @Override
    public void run() {
        load();
    }

    private void load() {
        File file = new File(filepath, filename);
        try {
            BufferedReader reader = new BufferedReader( new FileReader(file));

            String line;
            while((line = reader.readLine()) != null){
                Thread.sleep(250);

                for (LoadListener listener : listeners) {
                    listener.NumLoaded(Integer.parseInt(line));
                }
            }
            reader.close();
        }
        catch (IOException ex) {
            Log.e("Loading IOException", ex.getMessage());
        }
        catch (InterruptedException ex) {
            Log.e("Loading InterruptedException", ex.getMessage());
        }
    }
}

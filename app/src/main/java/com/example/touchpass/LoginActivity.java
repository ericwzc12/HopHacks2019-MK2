package com.example.touchpass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {


    TextView textView;
    private ArrayList<String> mURLs = new ArrayList<>();
    private ArrayList<String> mUsernames = new ArrayList<>();
    private String hash;
    private String URLIn;
    private String userIn;
    private static File path;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        hash = intent.getStringExtra("hash");

        path = new File(LoginActivity.this.getFilesDir(), "config.txt");
        initLists();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file = new File(LoginActivity.this.getFilesDir(), "config.txt");

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Enter URL");

                // Set up the input
                final EditText input = new EditText(LoginActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        URLIn = input.getText().toString();

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Enter Username");

                        // Set up the input
                        final EditText input = new EditText(LoginActivity.this);
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userIn = input.getText().toString();

                                try {
                                    FileOutputStream fileinput = new FileOutputStream(file, true);
                                    PrintStream printstream = new PrintStream(fileinput);
                                    if(URLIn == null || userIn == null){
                                        fileinput.close();
                                    } else{
                                        printstream.print(URLIn+"\n");
                                        printstream.print(userIn+"\n");
                                        fileinput.close();
                                        initLists();
                                    }

                                } catch (Exception e) {
                                    Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void initLists(){
        mURLs.clear();
        mUsernames.clear();

        mURLs.add("Default");
        mUsernames.add("");

        String line;

        try {
            FileInputStream fileInputStream = new FileInputStream (new File(LoginActivity.this.getFilesDir(), "config.txt"));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            bufferedReader.readLine();
            bufferedReader.readLine();

            while ( (line = bufferedReader.readLine()) != null )
            {
                mURLs.add(line);
                line = bufferedReader.readLine();
                mUsernames.add(line);
            }
            fileInputStream.close();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            Log.d("FileNotFoundException ", ex.getMessage());
        }
        catch(IOException ex) {
            Log.d("IOException ", ex.getMessage());
        }

        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mURLs, mUsernames, hash);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected static void deleteFromFile(ArrayList<String> mURLs, ArrayList<String> mUsernames){
        try {
            FileOutputStream fileinput = new FileOutputStream(path, false);
            PrintStream printstream = new PrintStream(fileinput);
            for(int i = 0; i < mURLs.size(); i++){
                printstream.append(mURLs.get(i) + "\n");
                printstream.append(mUsernames.get(i) + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        finish();
    }
}

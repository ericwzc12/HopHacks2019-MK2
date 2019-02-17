package com.example.touchpass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mUrlText;
    private ArrayList<String> mUsername;
    private Context mContext;
    private String sha256hex;
    private final String server_url = "http://35.188.254.68:8080";

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mUrlText, ArrayList<String> mUsername, String sha256hex) {
        this.mUrlText = mUrlText;
        this.mUsername = mUsername;
        this.mContext = mContext;
        this.sha256hex = sha256hex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
         viewHolder.urlText.setText(mUrlText.get(i));
         viewHolder.username.setText(mUsername.get(i));
         final int index = i;
         viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 final RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                 StringRequest stringRequest = new StringRequest(Request.Method.GET, server_url + "/send?username=" + mUsername.get(index) + "&master=" + sha256hex, new Response.Listener<String>() {

                     @Override
                     public void onResponse(String response) {
                         pushSuccess();
                         requestQueue.stop();
                     }
                 }, new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         error.printStackTrace();
                         pushFail();
                         requestQueue.stop();
                     }
                 });
                 requestQueue.add(stringRequest);
             }
         });
         viewHolder.parentLayout.setOnLongClickListener(new View.OnLongClickListener(){
             @Override
             public boolean onLongClick(View v){
                 AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                 builder.setTitle("Delete this entry?");

// Set up the input
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

// Set up the buttons
                 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         if(!mUrlText.get(index).equals("Default")) {
                             mUrlText.remove(index);
                             mUsername.remove(index);
                             LoginActivity.deleteFromFile(mUrlText, mUsername);
                             notifyDataSetChanged();
                         } else{
                             noDeleteDefault();
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
                 return true;
             }
             });
    }

    @Override
    public int getItemCount() {
        return mUrlText.size();
    }

    private void noDeleteDefault(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Can't delete Default");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void pushSuccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Successfully sent login request");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void pushFail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Unable to send login request... check connection");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView urlText;
        TextView username;
        CardView parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            urlText = itemView.findViewById(R.id.url_text);
            username = itemView.findViewById(R.id.username_text);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}

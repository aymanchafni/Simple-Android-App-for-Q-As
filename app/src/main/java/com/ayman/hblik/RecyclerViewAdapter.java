package com.ayman.hblik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> questions;
    private ArrayList<String> answerNbrs;
    private ArrayList<String> ids;
    private Context context;

    public RecyclerViewAdapter(Context context, ArrayList<String> questions, ArrayList<String> answerNbrs, ArrayList<String> ids) {
        this.questions = questions;
        this.answerNbrs = answerNbrs;
        this.context = context;
        this.ids=ids;
        Log.d(TAG, "RecyclerViewAdapter: "+questions+this.questions);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_activity, parent,false);
       ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.questionI.setText(questions.get(position));
        holder.answerNbr.setText("Number of answers  :" + answerNbrs.get(position));
        holder.item_user_activity.setOnClickListener(new View.OnClickListener() {
        int id=Integer.parseInt(ids.get(position));

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, QuestionStatsActivity.class);
                Bundle b =new Bundle();
                b.putInt("id_question",id);
                b.putString("question",questions.get(position));
                b.putInt("answerNbr",Integer.parseInt(answerNbrs.get(position)));
                i.putExtras(b);
                context.startActivity(i);
            }
        });

                            }




    @Override
    public int getItemCount()
    {

        Log.d(TAG, "getItemCount: called. "+questions.size());
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView questionI,answerNbr;
        LinearLayout item_user_activity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionI=itemView.findViewById(R.id.questionI);
            answerNbr=itemView.findViewById(R.id.answerNbr);
            item_user_activity=itemView.findViewById(R.id.item_user_activity);
        }
    }

}

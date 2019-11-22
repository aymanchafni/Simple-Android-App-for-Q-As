package com.ayman.hblik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static ArrayList<String> questions;
    private static ArrayList<String> answerNbrs;
    private static ArrayList<String> ids;
    private  Context context;

    RecyclerViewAdapter(Context context, ArrayList<String> quest, ArrayList<String> ans, ArrayList<String> ids0) {
        questions = quest;
        answerNbrs = ans;
        this.context = context;
        ids = ids0;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.questionI.setText(questions.get(position));
        holder.answerNbr.setText(context.getResources().getString(R.string.number_answers, answerNbrs.get(position)));
        holder.item_user_activity.setOnClickListener(new View.OnClickListener() {
            int id = Integer.parseInt(ids.get(position));

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, QuestionStatsActivity.class);
                Bundle b = new Bundle();
                b.putInt("id_question", id);
                b.putString("question", questions.get(position));
                b.putInt("answerNbr", Integer.parseInt(answerNbrs.get(position)));
                i.putExtras(b);
                context.startActivity(i);
            }
        });

    }


    @Override
    public int getItemCount() {

        return questions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionI, answerNbr;
        LinearLayout item_user_activity;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionI = itemView.findViewById(R.id.questionI);
            answerNbr = itemView.findViewById(R.id.answerNbr);
            item_user_activity = itemView.findViewById(R.id.item_user_activity);
        }
    }

}

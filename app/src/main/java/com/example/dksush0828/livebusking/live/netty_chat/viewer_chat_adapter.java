package com.example.dksush0828.livebusking.live.netty_chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dksush0828.livebusking.R;

import java.util.ArrayList;
import java.util.List;

public class viewer_chat_adapter extends RecyclerView.Adapter<viewer_chat_adapter.ViewHolder>{


    // 생성자.
    private Context context;
    private List<chat_item> items;

    public viewer_chat_adapter(Context context, List<chat_item> items){
        this.context = context;
        this.items = items;
    }

    public <E> viewer_chat_adapter(ArrayList<E> es, Context applicationContext, int i) {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        chat_item item = items.get(position);

        holder.chat_nickname.setText(item.getNickname());
        holder.chat_msg.setText(item.getMsg());
        Log.v("why : ",item.getMsg());


    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
       TextView chat_nickname, chat_msg;

        public ViewHolder(View itemView) {
            super(itemView);
            chat_nickname = itemView.findViewById(R.id.chat_nickname);
            chat_msg = itemView.findViewById(R.id.chat_msg);

        }
    }
}

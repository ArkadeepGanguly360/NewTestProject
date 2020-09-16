package com.example.testproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testproject.R;
import com.example.testproject.Model.UserListModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    private Context mContext;
    private List<UserListModel> userListArrList;

    public UserListAdapter(Context mContext, ArrayList<UserListModel> userListArrList) {

        this.mContext = mContext;
        this.userListArrList = userListArrList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_username;
        ImageView imgv_user;

        public MyViewHolder(View view) {
            super(view);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            imgv_user = (ImageView) view.findViewById(R.id.imgv_user);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userlist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_username.setText(userListArrList.get(position).getFirst_name() + " " + userListArrList.get(position).getLast_name());

        Picasso.with(mContext).load(userListArrList.get(position).getAvatar()).into(holder.imgv_user);
    }

    @Override
    public int getItemCount() {
        return userListArrList.size();
    }
}

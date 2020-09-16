package com.example.testproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.testproject.Adapter.UserListAdapter;
import com.example.testproject.Base.Url;
import com.example.testproject.Model.UserListModel;
import com.example.testproject.Util.CheckConnectivity;
import com.example.testproject.Util.DialogView;
import com.example.testproject.Util.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class UserListFragment extends Fragment {

    Context mContext;
    RecyclerView recycler_userlist;
    DialogView dialogView;
    ArrayList<UserListModel> userListArrList = new ArrayList<UserListModel>();
    RequestQueue mQueue;
    private String TAG = "UserListFragment";
    private int pagenumber = 1;
    private int visibleitemcount,totalitemcount,pastvisibleitems;
    private boolean loading= true;
    RecyclerView.LayoutManager mLayoutManager;
    CheckConnectivity checkConnectivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        mContext = getActivity();
        dialogView = new DialogView();
        mQueue = Volley.newRequestQueue(getActivity());
        checkConnectivity = new CheckConnectivity();
        recycler_userlist = view.findViewById(R.id.recycler_userlist);

        mLayoutManager = new LinearLayoutManager(mContext);
        recycler_userlist.setLayoutManager(mLayoutManager);
        recycler_userlist.setItemAnimator(new DefaultItemAnimator());

        recycler_userlist.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recycler_userlist, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(mContext,UserDetailsActivity.class);
                intent.putExtra("UserId", userListArrList.get(position).getId());
                intent.putExtra("Email", userListArrList.get(position).getEmail());
                intent.putExtra("FirstName", userListArrList.get(position).getFirst_name());
                intent.putExtra("LastName", userListArrList.get(position).getLast_name());
                intent.putExtra("Avatar", userListArrList.get(position).getAvatar());
               startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        if (checkConnectivity.isOnline(mContext)) {
            getUserList(pagenumber);
        }
        else {
            Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void getUserList(int pagenumber) {

        dialogView.showCustomSpinProgress(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Url.urlUserList + pagenumber,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jObject = jsonArray.getJSONObject(i);

                                UserListModel userList = new UserListModel();
                                userList.setId(jObject.optString("id"));
                                userList.setEmail(jObject.optString("email"));
                                userList.setFirst_name(jObject.optString("first_name"));
                                userList.setLast_name(jObject.optString("last_name"));
                                userList.setAvatar(jObject.optString("avatar"));

                                userListArrList.add(userList);
                            }

                            recycler_userlist.setAdapter(new UserListAdapter(mContext, userListArrList));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        dialogView.dismissCustomSpinProgress();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogView.dismissCustomSpinProgress();
                System.out.println("Something went wrong!");
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(60), 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        stringRequest.setTag(TAG);
        mQueue.add(stringRequest);

        pagination();
    }

    private void pagination() {

        recycler_userlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0)
                {
                    visibleitemcount = mLayoutManager.getChildCount();
                    totalitemcount = mLayoutManager.getItemCount();
                    pastvisibleitems = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();

                    if(loading)
                    {
                        if((visibleitemcount + pastvisibleitems) >= totalitemcount)
                        {
                            loading = false;
                            pagenumber += 1;
                            if (checkConnectivity.isOnline(mContext)) {
                                getUserList(pagenumber);
                            }
                            else {
                                Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });
    }
}



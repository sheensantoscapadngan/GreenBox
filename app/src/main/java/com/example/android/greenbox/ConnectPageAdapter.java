package com.example.android.greenbox;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ConnectPageAdapter extends RecyclerView.Adapter<ConnectPageAdapter.ViewHolder> {

    private ArrayList<String> addressList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private Context context;
    private ConnectCallback connectCallback;

    public ConnectPageAdapter(ArrayList<String> addressList, ArrayList<String> nameList,Context context) {
        this.addressList = addressList;
        this.nameList = nameList;
        this.context = context;
        connectCallback = ((ConnectCallback)context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connect_recycler_layout,parent,false);
        ConnectPageAdapter.ViewHolder viewHolder = new ConnectPageAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.name.setText(nameList.get(position));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connectCallback.notifyBluetoothConnect(addressList.get(position));

            }
        });

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ConstraintLayout layout;

        public ViewHolder(View itemView) {

            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewConnectListName);
            layout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayoutConnectList);

        }
    }

    public static interface ConnectCallback{
        void notifyBluetoothConnect(String address);
    }

}

package com.hihi.mybad.chatapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Long on 23/4/2017.
 */

public class ListUserAdapter extends ArrayAdapter<User>{
    Context context;
    ArrayList<User> listUser = new ArrayList<>();

    public ListUserAdapter(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        this.context = context;
        this.listUser = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder = null;
        if (rowView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.layout_row_detail,null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) rowView.findViewById(R.id.profileImage);
            viewHolder.name = (TextView) rowView.findViewById(R.id.name);
            viewHolder.email =  (TextView) rowView.findViewById(R.id.email);
            rowView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        User user = listUser.get(position);
        Picasso.with(context).load(user.getImage()).into(viewHolder.image);
        viewHolder.name.setText(user.getName());
        viewHolder.email.setText(user.getEmail());

        return rowView;

    }
    static class ViewHolder{
        ImageView image;
        TextView name;
        TextView email;
    }
}

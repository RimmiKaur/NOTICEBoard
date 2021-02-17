package com.example.noticeboard.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noticeboard.R;

import java.util.ArrayList;


public class List_of_groups_Fragment extends Fragment  {

    private ListView listView;

    ArrayList<Listview_model1> model= new ArrayList<>();;
    List_custom_view_Adapter1 list_custom_view_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_of_groups_, container, false);

        listView = view.findViewById(R.id.List_view_all_groups);
        model.add(new Listview_model1(R.drawable.selsel, "EXTC 2017-2021"));

        list_custom_view_adapter = new List_custom_view_Adapter1(getContext(), model);
        listView.setAdapter(list_custom_view_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new All_notices_Fragment()).addToBackStack(null).commit();
            }
        });

        return view;
    }

    public class Listview_model1 {
        int group_image;
        String group_name;

        public Listview_model1(int group_image, String group_name) {
            this.group_image = group_image;
            this.group_name = group_name;
        }

        public int getGroup_image() {
            return group_image;
        }

        public void setGroup_image(int group_image) {
            this.group_image = group_image;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }


    }

    public class List_custom_view_Adapter1 extends BaseAdapter {

        private Context context;
        private ArrayList<Listview_model1> models;

        public List_custom_view_Adapter1(Context context, ArrayList<Listview_model1> models) {
            this.context = context;
            this.models = models;
        }

        @Override
        public int getCount() {
            return models.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.all_groups_list_view,parent, false);
            TextView group_name_textview = convertView.findViewById(R.id.group_name_textview);
            ImageView group_icon = convertView.findViewById(R.id.group_icon);
            group_name_textview.setText(model.get(position).getGroup_name());
            group_icon.setImageResource(model.get(position).getGroup_image());
            return convertView;
        }

    }
}


package com.a11658.android_listview_down_refresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author Qiang
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<APKEntity> list;

    public MyAdapter(Context context, List<APKEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder;
        if (convertView == null) {
            viewHoder = new ViewHoder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_layout, parent, false);
            viewHoder.data = convertView.findViewById(R.id.data);
            viewHoder.image = convertView.findViewById(R.id.image);
            viewHoder.text = convertView.findViewById(R.id.text);
            convertView.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }

        viewHoder.data.setText(list.get(position).getData());
        viewHoder.image.setImageResource(list.get(position).getImage());
        viewHoder.text.setText(list.get(position).getTest());

        return convertView;
    }

    class ViewHoder {
        ImageView image;
        TextView data;
        TextView text;
    }
}

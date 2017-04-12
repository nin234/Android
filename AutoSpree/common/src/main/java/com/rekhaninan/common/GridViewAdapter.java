package com.rekhaninan.common;



import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
        import android.widget.TextView;

import static com.rekhaninan.common.Constants.PICTURE_VW;
import static com.rekhaninan.common.Constants.SHARE_PICTURE_VW;

public class GridViewAdapter extends ArrayAdapter<ImageItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList<>();
    private int viewType;
    private String app_name;
    private boolean[] thumbnailsselection;

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        thumbnailsselection = new boolean[data.size()];
        Arrays.fill(thumbnailsselection, Boolean.FALSE);
    }

    public void setParams(int vtyp, String appname)
    {
        viewType = vtyp;
        app_name = appname;
        return;
    }

    public boolean[] getSelectedItems()
    {

        return thumbnailsselection;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        switch (viewType) {

            case PICTURE_VW: {
                View row = convertView;
                ViewHolder holder;

                if (row == null) {
                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                    row = inflater.inflate(layoutResourceId, parent, false);
                    holder = new ViewHolder();

                    holder.image = (ImageView) row.findViewById(R.id.image);
                    row.setTag(holder);
                } else {
                    holder = (ViewHolder) row.getTag();
                }


                ImageItem item = data.get(position);

                holder.image.setImageBitmap(item.getImage());
                return row;
            }

            case SHARE_PICTURE_VW:
            {
                ShareViewHolder holder;
                if (convertView == null) {
                    holder = new ShareViewHolder();
                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                    convertView = inflater.inflate(
                            R.layout.photo_roll_select, null);
                    holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                    holder.checkbox = (CheckBox) convertView.findViewById(R.id.imageCheckBox);

                    convertView.setTag(holder);
                }
                else {
                    holder = (ShareViewHolder) convertView.getTag();
                }
                holder.checkbox.setId(position);
                holder.imageview.setId(position);
                holder.checkbox.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        CheckBox cb = (CheckBox) v;
                        int id = cb.getId();
                        if (thumbnailsselection[id]){
                            cb.setChecked(false);
                            thumbnailsselection[id] = false;
                        } else {
                            cb.setChecked(true);
                            thumbnailsselection[id] = true;
                        }
                    }
                });
                ImageItem item = data.get(position);
                holder.imageview.setImageBitmap(item.getImage());
                holder.checkbox.setChecked(thumbnailsselection[position]);
                holder.id = position;
                return convertView;
            }

            default:
                break;
        }
        return null;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }

    class ShareViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }
}
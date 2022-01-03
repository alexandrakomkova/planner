package oop.dayplanner3.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.List;

import oop.dayplanner3.R;
import oop.dayplanner3.model.Category;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Category> categoryList;
    Integer idx=2;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean containsName(final List<Category> list, final String name){
        return list.stream().anyMatch(o -> o.getName().equals(name));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getCategoryListPosition(String name){
        if(containsName(categoryList, name)){
            idx = categoryList.indexOf(name);
        }
        return idx;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_category, viewGroup, false);

        TextView txtName = rootView.findViewById(R.id.name);
        ImageView image = rootView.findViewById(R.id.image);

        txtName.setText(categoryList.get(i).getName());
        image.setImageResource(categoryList.get(i).getImage());

        return rootView;
    }
}

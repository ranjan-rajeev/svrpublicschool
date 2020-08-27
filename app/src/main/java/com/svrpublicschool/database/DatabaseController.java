package com.svrpublicschool.database;

import android.content.Context;

import com.svrpublicschool.R;
import com.svrpublicschool.ui.main.NavigationItemEntity;

import java.util.ArrayList;
import java.util.List;

public class DatabaseController {
    Context context;

    public DatabaseController(Context context) {
        this.context = context;
    }

    public List<NavigationItemEntity> getDshboardList() {
        int i = 0;
        List<NavigationItemEntity> list = new ArrayList<>();
        list.add(new NavigationItemEntity(++i, R.drawable.ic_menu_gallery, "HOME"));
        list.add(new NavigationItemEntity(++i, R.drawable.ic_menu_gallery, "FACULTY"));
        list.add(new NavigationItemEntity(++i, R.drawable.ic_menu_gallery, "GALLERY"));
        list.add(new NavigationItemEntity(++i, R.drawable.ic_menu_gallery, "FACILITY"));
        list.add(new NavigationItemEntity(++i, R.drawable.ic_menu_gallery, "ABOUT US"));
        list.add(new NavigationItemEntity(++i, R.drawable.ic_menu_gallery, "CONTACT"));
        return list;
    }
}

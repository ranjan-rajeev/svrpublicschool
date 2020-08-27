package com.svrpublicschool.ui.main;

public class NavigationItemEntity {
    int id;
    boolean isSelected;
    int icon;
    String title;

    public NavigationItemEntity() {
        this.isSelected = false;
    }

    public NavigationItemEntity(int id, int icon, String title) {
        this.id = id;
        this.icon = icon;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

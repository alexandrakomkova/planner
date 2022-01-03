package oop.dayplanner3.model;

import java.util.ArrayList;
import java.util.List;

import oop.dayplanner3.R;

public class Data {
    public static List<Category> getCategoryList() {
        List<Category> categoryList = new ArrayList<>();

        Category work = new Category();
        work.setName("Work");
        work.setImage(R.drawable.work);
        categoryList.add(work);

        Category sleep = new Category();
        sleep.setName("Sleep");
        sleep.setImage(R.drawable.sleep);
        categoryList.add(sleep);

        Category study = new Category();
        study.setName("Study");
        study.setImage(R.drawable.study);
        categoryList.add(study);

        Category eat = new Category();
        eat.setName("Eat Time");
        eat.setImage(R.drawable.eattime);
        categoryList.add(eat);

        Category breakTime = new Category();
        breakTime.setName("Break Time");
        breakTime.setImage(R.drawable.breaktime);
        categoryList.add(breakTime);

        Category shopping = new Category();
        shopping.setName("Shopping");
        shopping.setImage(R.drawable.shop);
        categoryList.add(shopping);

        Category family = new Category();
        family.setName("Family");
        family.setImage(R.drawable.family);
        categoryList.add(family);

        Category friends = new Category();
        friends.setName("Friends");
        friends.setImage(R.drawable.friends);
        categoryList.add(friends);

        return categoryList;
    }
}

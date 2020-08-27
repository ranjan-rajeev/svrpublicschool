package com.svrpublicschool.ui.book;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.ItemOffsetDecoration;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.firebase.FirebaseHelper;
import com.svrpublicschool.models.BooksEntity;
import com.svrpublicschool.ui.book.adapter.BooksAdapter;

import java.util.List;

public class BookFragment extends BaseFragment {

    View view;
    BooksAdapter bookAdapter;
    RecyclerView rvBooks;
    TextView tvAdmisson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_books, container, false);
        initialise(view);
        new LoadLocalBooks().execute();
        return view;
    }

    private void initialise(View view) {
        tvAdmisson = view.findViewById(R.id.tvAdmisson);
        tvAdmisson.setSelected(true);
        rvBooks = view.findViewById(R.id.rvBooks);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        rvBooks.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvBooks.addItemDecoration(itemDecoration);
    }

    public class LoadLocalBooks extends AsyncTask<Void, Void, List<BooksEntity>> {

        @Override
        protected List<BooksEntity> doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                List<BooksEntity> booksEntities = gson.fromJson(FirebaseHelper.getBookList(), new TypeToken<List<BooksEntity>>() {
                }.getType());
                return booksEntities;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<BooksEntity> list) {
            super.onPostExecute(list);
            if (!Utility.isListEmpty(list)) {
                bookAdapter = new BooksAdapter(BookFragment.this.getActivity(), list);
                rvBooks.setAdapter(bookAdapter);
            }
        }
    }

}

package com.svrpublicschool.ui.book;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.ItemOffsetDecoration;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.database.DatabaseClient;
import com.svrpublicschool.models.BooksEntity;
import com.svrpublicschool.ui.book.adapter.BookDetailsAdapter;

import java.util.List;

public class BookDetailsFragment extends BaseFragment {

    View view;
    BookDetailsAdapter bookDetailsAdapter;
    RecyclerView rvBooks;
    TextView tvAdmisson;
    SharedPrefManager sharedPrefManager;
    TextView tvBookName;
    BooksEntity booksEntity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_books_details, container, false);
        sharedPrefManager = SharedPrefManager.getInstance(this.getActivity());

        initialise(view);
        getIntentExtraData();
        new LoadBooksDetails().execute();
        return view;
    }

    private void getIntentExtraData() {
        try {
            booksEntity = (BooksEntity) getArguments().getSerializable(Constants.INTENT_PARAM_BOOK);
            tvBookName.setText(booksEntity.getSubject() + " ( class " + booksEntity.getClassX() + " )");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialise(View view) {
        tvBookName = view.findViewById(R.id.tvBookName);
        tvAdmisson = view.findViewById(R.id.tvAdmisson);
        tvAdmisson.setSelected(true);
        rvBooks = view.findViewById(R.id.rvBooks);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        rvBooks.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvBooks.addItemDecoration(itemDecoration);
    }


    public class LoadBooksDetails extends AsyncTask<Void, Void, List<BooksEntity>> {

        @Override
        protected List<BooksEntity> doInBackground(Void... voids) {
            try {
                return DatabaseClient.getInstance(BookDetailsFragment.this.getActivity()).getAppDatabase().booksDao().getBookDetails(booksEntity.getClassX(), booksEntity.getSubject(), false, false);
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(List<BooksEntity> list) {
            super.onPostExecute(list);
            if (list != null) {
                Logger.d("List fetched from local  : " + list.size());
                bookDetailsAdapter = new BookDetailsAdapter(BookDetailsFragment.this.getActivity(), list);
                rvBooks.setAdapter(bookDetailsAdapter);
            }
        }
    }


}

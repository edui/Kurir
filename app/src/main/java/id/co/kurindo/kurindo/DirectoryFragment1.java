package id.co.kurindo.kurindo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.adapter.GalleryAdapter;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.model.ImageModel;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;
import id.co.kurindo.kurindo.task.LoadNewsTask;

/**
 * Created by DwiM on 11/9/2016.
 */
public class DirectoryFragment1 extends Fragment {
    GalleryAdapter mAdapter;
    RecyclerView mRecyclerView;
    ArrayList<ImageModel> data = new ArrayList<>();
    private ListenableAsyncTask loadNewsTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.directory_layout, null);
        loadNewsTask = (ListenableAsyncTask) new LoadNewsTask(getContext());
        loadNewsTask.listenWith(new ListenableAsyncTask.AsyncTaskListener() {
            @Override
            public void onPostExecute(Object o) {
                List<News> newsList = (List<News>)o;
                Log.i("loadNewsTask","newsList size:"+newsList.size());
                if(newsList != null && newsList.size() > 0){
                    data.clear();
                    for (int i = 0; i < newsList.size(); i++) {
                        News n = newsList.get(i);
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(n.getTitle());
                        imageModel.setUrl(n.getImg());
                        data.add(imageModel);
                    }
                }else{
                    data.clear();
                    ImageModel imageModel = new ImageModel();
                    imageModel.setName("test");
                    imageModel.setDrawable(R.drawable.cover1);
                    data.add(imageModel);
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.setHasFixedSize(true);


        mAdapter = new GalleryAdapter(getContext(), data);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getContext(), DetailImageActivity.class);
                        intent.putParcelableArrayListExtra("orders", data);
                        intent.putExtra("pos", position);
                        startActivity(intent);
                    }
                }));

        loadNewsTask.execute("direktori");
        return  view;
    }
}

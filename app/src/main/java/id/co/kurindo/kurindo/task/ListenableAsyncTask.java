package id.co.kurindo.kurindo.task;

import android.os.AsyncTask;

/**
 * Created by DwiM on 11/12/2016.
 */
public abstract class ListenableAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    @Override
    protected final void onPostExecute(Result result) {
        notifyListenerOnPostExecute(result);
    }

    private AsyncTaskListener<Result> mListener;
    public interface AsyncTaskListener<Result>{
        public void onPostExecute(Result result);
    }
    public void listenWith(AsyncTaskListener<Result> l){
        mListener = l;
    }
    private void notifyListenerOnPostExecute(Result result){
        if(mListener != null)
            mListener.onPostExecute(result);
    }
}

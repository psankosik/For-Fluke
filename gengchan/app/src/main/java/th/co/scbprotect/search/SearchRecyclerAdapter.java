package th.co.scbprotect.search;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import th.co.scbprotect.R;
import th.co.scbprotect.util.Configuration;
import th.co.scbprotect.util.PostConnector;

public class SearchRecyclerAdapter extends RecyclerView.Adapter{

    private ArrayList<SearchResultItem> mSearchResultList;
    Context context;

    public SearchRecyclerAdapter(ArrayList<SearchResultItem> SearchResultList, Context ct) {
        mSearchResultList = SearchResultList;
        context = ct;
    }

    @Override
    public int getItemViewType(int position) {

        if (mSearchResultList.get(position).getFileType().equals("\"PDF\"")) {
            return 1;
        } else if (mSearchResultList.get(position).getFileType().equals("\"IMG\"")){
            return 2;
        } else {
            return 3;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == 1) {
            view = layoutInflater.inflate(R.layout.search_item_pdf, parent, false);
            return new ViewHolderOne(view);
        } else if (viewType == 2) {
            view = layoutInflater.inflate(R.layout.search_item_image, parent, false);
            return new ViewHolderTwo(view);
        } else {
            view = layoutInflater.inflate(R.layout.search_item_video, parent, false);
            return new ViewHolderThree(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (mSearchResultList.get(position).getFileType().equals("\"PDF\"")) {
            ViewHolderOne viewHolderOne = (ViewHolderOne) holder;
            viewHolderOne.fileTitle.setText(mSearchResultList.get(position).getFileContent().substring(1,30));
            viewHolderOne.filePreview.setText(mSearchResultList.get(position).getFileContent().substring(1,100));

            viewHolderOne.fileTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SearchDetailActivity.class);
                    intent.putExtra("data1", mSearchResultList.get(position).getFileContent().substring(1,30));
                    intent.putExtra("data2", mSearchResultList.get(position).getFileStage());
                    intent.putExtra("data3", mSearchResultList.get(position).getFileContent().substring(1));
                    context.startActivity(intent);
                }
            });
        } else if (mSearchResultList.get(position).getFileType().equals("\"IMG\"")){
            ViewHolderTwo viewHolderTwo = (ViewHolderTwo) holder;
            viewHolderTwo.fileTitle.setText(mSearchResultList.get(position).getFileContent().substring(1,30));
            viewHolderTwo.filePreview.setText(mSearchResultList.get(position).getFileContent().substring(1,100));

            viewHolderTwo.fileTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context , SearchDetailActivity.class);
                    intent.putExtra("data1", mSearchResultList.get(position).getFileContent().substring(1,30));
                    intent.putExtra("data2", mSearchResultList.get(position).getFileStage());
                    intent.putExtra("data3", mSearchResultList.get(position).getFileContent().substring(1));
                    context.startActivity(intent);
                }
            });
        } else {
            ViewHolderThree viewHolderThree = (ViewHolderThree) holder;
            viewHolderThree.fileTitle.setText(mSearchResultList.get(position).getFileContent().substring(1,30));
            viewHolderThree.filePreview.setText(mSearchResultList.get(position).getFileContent().substring(1,100));

            viewHolderThree.fileTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context , SearchDetailActivity.class);
                    intent.putExtra("data1",mSearchResultList.get(position).getFileContent().substring(1,30));
                    intent.putExtra("data2", mSearchResultList.get(position).getFileStage());
                    intent.putExtra("data3", mSearchResultList.get(position).getFileContent().substring(1));
                    context.startActivity(intent);

                    PostConnector postConnector = new PostConnector(
                            Configuration.getFilePath(),
                            String.format("{\"key\":\"%s\"}", "0ab0a34d-ef7d-45d0-a018-9825bdf26b1d.pdf"),
                            "accessToken",
                            new getFileHandler());
                    postConnector.execute();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSearchResultList.size();
    }

    private static class getFileHandler implements PostConnector.DataHandler {
        @Override
        public void processData(String data) throws IOException {
            String FILENAME = "user_details";
            String name = "suresh";

            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File myFile = new File(folder, FILENAME);
            FileOutputStream fstream = new FileOutputStream(myFile);
            fstream.write(name.getBytes());
            fstream.close();

        }
    }

    static class ViewHolderOne extends RecyclerView.ViewHolder {

        TextView fileTitle, filePreview;
        public ViewHolderOne(@NonNull View itemView) {
            super(itemView);
            fileTitle = itemView.findViewById(R.id.searchresult_pdf_tv_title);
            filePreview = itemView.findViewById(R.id.searchresult_pdf_tv_preview);
        }
    }

    static class ViewHolderTwo extends RecyclerView.ViewHolder {

        TextView fileTitle, filePreview;
        public ViewHolderTwo(@NonNull View itemView) {
            super(itemView);
            fileTitle = itemView.findViewById(R.id.searchresult_image_tv_title);
            filePreview = itemView.findViewById(R.id.searchresult_image_tv_preview);
        }
    }

    static class ViewHolderThree extends RecyclerView.ViewHolder {

        TextView fileTitle, filePreview;
        public ViewHolderThree(@NonNull View itemView) {
            super(itemView);
            fileTitle = itemView.findViewById(R.id.searchresult_video_tv_title);
            filePreview = itemView.findViewById(R.id.searchresult_video_tv_preview);
        }
    }
}

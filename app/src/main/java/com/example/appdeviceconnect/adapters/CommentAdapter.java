package com.example.appdeviceconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.models.Comment;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.customerName.setText(comment.getCustomerName());
        holder.commentText.setText(comment.getText());
        holder.ratingText.setText("Rating: " + comment.getRanking());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, commentText, ratingText;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customerName);
            commentText = itemView.findViewById(R.id.commentText);
            ratingText = itemView.findViewById(R.id.ratingText);
        }
    }
}

package org.affordablehousing.chi.housingapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.databinding.NoteItemBinding;
import org.affordablehousing.chi.housingapp.model.Note;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<? extends Note> mNoteList;

    public void setNoteList(final List<? extends Note> notes) {
        if (mNoteList == null) {
            mNoteList = notes;
            notifyItemRangeInserted(0, notes.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mNoteList.size();
                }

                @Override
                public int getNewListSize() {
                    return notes.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Note old = mNoteList.get(oldItemPosition);
                    Note note = notes.get(newItemPosition);
                    return old.getNoteId() == note.getNoteId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Note old = mNoteList.get(oldItemPosition);
                    Note note = notes.get(newItemPosition);
                    return old.getNoteId() == note.getNoteId()
                            && old.getPostedDate() == note.getPostedDate()
                            && Objects.equals(old.getText(), note.getText());
                }
            });
            mNoteList = notes;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.note_item,
                        parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.binding.setNote(mNoteList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mNoteList == null ? 0 : mNoteList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        final NoteItemBinding binding;

        NoteViewHolder(NoteItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
